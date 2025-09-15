package com.talentstream.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.talentstream.entity.VideoMetadata;

@Repository
public interface VideoMetadataRepository extends JpaRepository<VideoMetadata, Long> {

    @Query(value = """
            WITH keyword_watch_count AS (
                SELECT kw.keyword, COUNT(*) AS watch_count
                FROM applicant_video_watch_history wh
                JOIN video_metadata v2 ON wh.video_id = v2.video_id
                JOIN (
                    SELECT 'interview' AS keyword
                    UNION ALL SELECT 'softskill'
                    UNION ALL SELECT 'communication'
                ) kw ON LOWER(v2.title) LIKE CONCAT('%', kw.keyword, '%')
                WHERE wh.applicant_id = :applicantId
                GROUP BY kw.keyword
            ),
            skill_watch_count AS (
                SELECT LOWER(s.skill_name) AS keyword, COUNT(*) AS watch_count
                FROM applicant_video_watch_history wh
                JOIN video_metadata v2 ON wh.video_id = v2.video_id
                JOIN applicant_profile ap ON ap.applicantid = :applicantId
                JOIN applicant_profile_skills_required apsr ON apsr.profileid = ap.profileid
                JOIN applicant_skills s ON apsr.applicantskill_id = s.id
                WHERE LOWER(v2.title) LIKE CONCAT('%', LOWER(s.skill_name), '%')
                GROUP BY LOWER(s.skill_name)
            ),
            combined_watch_count AS (
                SELECT keyword, SUM(watch_count) AS watch_count
                FROM (
                    SELECT * FROM keyword_watch_count
                    UNION ALL
                    SELECT * FROM skill_watch_count
                ) all_counts
                GROUP BY keyword
            ),
            video_scores AS (
                SELECT v.video_id, v.s3url, v.tags, v.title,
                       CASE WHEN w.video_id IS NULL THEN 0 ELSE 1 END AS watched_flag,
                       MAX(CASE WHEN LOWER(v.title) LIKE CONCAT('%', LOWER(s.skill_name), '%')
                                THEN COALESCE(cwc.watch_count,0) ELSE 0 END) AS skill_count,
                       MAX(CASE WHEN LOWER(v.title) LIKE '%interview%'
                                 OR LOWER(v.title) LIKE '%softskill%'
                                 OR LOWER(v.title) LIKE '%communication%'
                                THEN COALESCE(cwc.watch_count,0) ELSE 0 END) AS general_count,
                       CASE WHEN LOWER(v.title) LIKE CONCAT('%', LOWER(s.skill_name), '%')
                            THEN 1 ELSE 0 END AS is_skill,
                       CASE WHEN LOWER(v.title) LIKE '%interview%'
                                 OR LOWER(v.title) LIKE '%softskill%'
                                 OR LOWER(v.title) LIKE '%communication%'
                            THEN 1 ELSE 0 END AS is_general
                FROM video_metadata v
                LEFT JOIN applicant_video_watch_history w
                    ON v.video_id = w.video_id AND w.applicant_id = :applicantId
                LEFT JOIN applicant_profile ap
                    ON ap.applicantid = :applicantId
                LEFT JOIN applicant_profile_skills_required apsr
                    ON apsr.profileid = ap.profileid
                LEFT JOIN applicant_skills s
                    ON apsr.applicantskill_id = s.id
                LEFT JOIN combined_watch_count cwc
                    ON cwc.keyword = LOWER(s.skill_name)
                    OR (cwc.keyword IN ('interview','softskill','communication')
                        AND LOWER(v.title) LIKE CONCAT('%', cwc.keyword, '%'))
                GROUP BY v.video_id, v.s3url, v.tags, v.title, watched_flag, is_skill, is_general
            )
            SELECT video_id, s3url, tags, title, watched_flag, is_skill, is_general,
                   GREATEST(skill_count, general_count) AS watch_count
            FROM video_scores
            WHERE is_skill = 1 OR is_general = 1
            ORDER BY
                watched_flag ASC,                            -- 1. Unwatched before watched
                GREATEST(skill_count, general_count) DESC,   -- 2. Trending inside each group
                is_skill DESC,                               -- 3. Skills before general
                video_id ASC                                 -- 4. Tie breaker
            LIMIT 15
            """, countQuery = "SELECT COUNT(*) FROM video_metadata", nativeQuery = true)
    List<Object[]> fetchRecommendedVideos(@Param("applicantId") Integer applicantId);
}
