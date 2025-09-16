package com.talentstream.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentstream.entity.Blog;
import com.talentstream.repository.BlogRepository;

@Service
public class BlogService {

    private static final String NEWS_BASE_URL = "https://newsapi.org/v2/everything";
    private static final String GEMINI_URL_TEMPLATE ="https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=%s";

    private final RestTemplate restTemplate;
    private final BlogRepository blogRepository;
    private final ObjectMapper mapper;
    private final FirebaseMessagingService firebaseMessagingService;

    @Value("${newsapi.key}")
    private String apiKey;

    @Value("${gemini.api.key}")
    private String geminiKey;

    public BlogService(BlogRepository blogRepository, FirebaseMessagingService firebaseMessagingService) {
        this.blogRepository = blogRepository;
        this.restTemplate = new RestTemplate();
        this.mapper = new ObjectMapper();
        this.firebaseMessagingService= firebaseMessagingService;
    }

    public void fetchAndSaveTechNews() {
        try {
            String url = buildNewsApiUrl();

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                System.err.println("Failed to fetch news. Status: " + response.getStatusCode());
                return;
            }

            JsonNode articles = mapper.readTree(response.getBody()).path("articles");

            if (articles.isEmpty()) {
                System.out.println("No new articles found.");
                return;
            }

            for (JsonNode article : articles) {
                String articleUrl = article.path("url").asText();
                String title = article.path("title").asText();

                if (blogRepository.findByUrlOrTitle(articleUrl, title).isPresent()) {
                    System.out.println("Skipping duplicate article (Title or URL already exists): " + title);
                    continue;
                }

                Blog blog = mapArticleToBlog(article);
                blogRepository.save(blog);
            }


        } catch (Exception e) {
            System.err.println("An error occurred while fetching or saving news: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildNewsApiUrl() {
        LocalDate today = LocalDate.now();
        LocalDate threeDaysAgo = today.minusDays(3);

        String query = URLEncoder.encode("Software OR Artificial Intelligence OR TECHNOLOGY", StandardCharsets.UTF_8);

        return NEWS_BASE_URL
                + "?apiKey=" + apiKey
                + "&q=" + query
                + "&searchIn=title,description"
                + "&domains=techcrunch.com,wired.com,theverge.com,engadget.com"
                + "&from=" + threeDaysAgo
                + "&to=" + today
                + "&language=en"
                + "&sortBy=publishedAt"
                + "&pageSize=10"
                + "&page=1";
    }


    private Blog mapArticleToBlog(JsonNode article) {
        String title = article.path("title").asText();
        String description = article.path("description").asText();
        String content = article.path("content").asText();
        
        String elaboratedContent = callGeminiForElaboration(title, description, content);

        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setDescription(description);
        blog.setAuthor(article.path("author").asText(null));
        blog.setContent(elaboratedContent);
        blog.setUrl(article.path("url").asText());
        blog.setImageUrl(article.path("urlToImage").asText());
        blog.setPublishedAt(article.path("publishedAt").asText());
        blog.setCreatedAt(LocalDateTime.now());
        return blog;
    }

   private String callGeminiForElaboration(String title, String description, String snippet) {
    try {
        String prompt = String.format(
                "Summarize this news into 5 to 6 simple paragraphs. " +
                "Keep only the main content with a good conclusion. Do not include headings, bullet points, " +
                "symbols, links, or tables. Use plain text only.\n\n" +
                "Title: %s\nDescription: %s\nContent snippet: %s\n\nBlog:",
                title, description, snippet
        );

        String geminiUrl = String.format(GEMINI_URL_TEMPLATE, geminiKey);

        // Build JSON safely using Map
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> contents = Map.of("parts", List.of(part));
        Map<String, Object> requestBody = Map.of("contents", List.of(contents));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                geminiUrl, HttpMethod.POST, request, String.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return snippet; // fallback if error
}

    public ResponseEntity<?> getActiveBlogs() {
        try {
            List<Blog> activeBlogs = blogRepository.findByIsActiveTrue();
            return ResponseEntity.ok(activeBlogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve active blogs");
        }
    }

    public ResponseEntity<?> getInActiveBlogs() {
        try {
            List<Blog> inactiveBlogs = blogRepository.findByIsActiveFalse();
            return ResponseEntity.ok(inactiveBlogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve inactive blogs");
        }
    }

    public ResponseEntity<?> getAllBlogs() {
        try {
            List<Blog> allBlogs = blogRepository.findAll();
            return ResponseEntity.ok(allBlogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve all blogs");
        }
    }


	public ResponseEntity<String> deleteBlog(UUID id) {
		try {
			Optional<Blog> optionalBlog = blogRepository.findById(id);
			if (optionalBlog.isPresent()) {
				blogRepository.delete(optionalBlog.get());
				return ResponseEntity.ok("Blog deleted successfully with id: " + id);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blog not found with id: " + id);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the blog with id: " + id);
		}
	}

	public ResponseEntity<String> updateBlog(UUID id, String author, boolean active) {
		try{
            Optional<Blog> optionalBlog = blogRepository.findById(id);
        if (optionalBlog.isPresent()) {
            Blog blog = optionalBlog.get();

            if (author != null && !author.trim().isEmpty()) {
                blog.setAuthor(author);
            }
            
            blog.setActive(active);
            blogRepository.save(blog);
            String title = "New Blog Update Today!";
            String body = "Check out the latest blog updates added today.";
           try {
        	   String result= firebaseMessagingService.sendNotificationToAll(title, body);
        	   System.out.println("result:" +result);
           }
           catch(Exception e){
        	  System.out.println(e.getMessage());
           }
         
         
            return ResponseEntity.ok("Blog updated successfully with id: " + id);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blog not found with id: " + id);
        }      
		}
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the blog with id: " + e.getMessage());
        }
		
	}
}
