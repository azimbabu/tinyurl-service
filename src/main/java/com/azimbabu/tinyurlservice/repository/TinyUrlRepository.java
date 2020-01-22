package com.azimbabu.tinyurlservice.repository;

import com.azimbabu.tinyurlservice.model.TinyUrl;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TinyUrlRepository extends CrudRepository<TinyUrl, String> {

  Optional<TinyUrl> findByShortUrl(String shortUrl);

  boolean existsByShortUrl(String shortUrl);
}
