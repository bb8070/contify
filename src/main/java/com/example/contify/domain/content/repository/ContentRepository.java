package com.example.contify.domain.content.repository;

import com.example.contify.domain.content.entity.Content;
import com.example.contify.domain.content.entity.ContentCategory;
import com.example.contify.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> , ContentRepositoryCustom {

    //DB 원자적 증가 ( lost update 방지 )
    //동시요청이 100개 와도 100이 증가함.
    //Modifying 은 JPQL update 는 영속성 컨텍스트와 싱크가 안맞을 수 있음
    // flushAutomatically : update 전 / cleanAutomatically :  update 후
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Content c set c.viewCount = c.viewCount+1 where c.id=:id")
    int increaseViewCountAtomic(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Content c set c.likeCount = c.likeCount+1 where c.id=:id")
    int increaseLikeCount(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Content c set c.likeCount = c.likeCount-1 where c.id=:id")
    int decreaseLikeCount(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Content c set c.bookmarkCount = c.bookmarkCount+1 where c.id=:id")
    int increaseBookmark(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Content c set c.bookmarkCount = CASE WHEN c.bookmarkCount-1 >0 THEN c.bookmarkCount-1 ELSE 0 END where c.id=:id")
    int decreaseBookmark(@Param("id") Long id);

//    @Modifying
 //   @Query("update Content c set c.viewCount = c.viewCount + :count where c.id = :id")
  //  void increaseViewCount(@Param("id") Long id, @Param("count") Long count);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Content c set c.viewCount = c.viewCount + :delta where c.id = :id")
    int increaseViewCount(@Param("id")Long id , @Param("delta") Long Delta);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Content c set c.bookmarkCount = CASE WHEN c.bookmarkCount + :delta >0 THEN c.bookmarkCount + :delta ELSE 0 END where c.id = :id")
    int increaseBookmarkCount(@Param("id") Long id , @Param("delta") Long Delta);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Content c set c.likeCount = CASE WHEN c.likeCount + :delta > 0 THEN c.likeCount +:delta ELSE 0 END where c.id = :id")
    int increaseLikeCount(@Param("id") Long id , @Param("delta") Long Delta);

    @Query("select c.id from Content c order by c.id desc")
    List<Long> findAllIds();

    @Query("select c.createdUser.id from Content c where c.id = :contentId")
    Long findAuthorIdByContentId(@Param("contentId") Long contentId);

    //IN조회
    List<Content> findByIdInAndCategory(List<Long> ids, ContentCategory category);
    List<Content> findByIdIn(List<Long> ids);

}
