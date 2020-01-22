package com.azimbabu.tinyurlservice.service;

import com.azimbabu.tinyurlservice.config.TinyUrlProperties;
import com.azimbabu.tinyurlservice.model.TinyUrl;
import com.azimbabu.tinyurlservice.model.User;
import com.azimbabu.tinyurlservice.model.UserUDT;
import com.azimbabu.tinyurlservice.repository.TinyUrlRepository;
import com.azimbabu.tinyurlservice.repository.UserRepository;
import com.datastax.driver.core.utils.UUIDs;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class TinyUrlService {

  private static final BigInteger BASE_62 = BigInteger.valueOf(62);
  private static final int HEX_BASE = 16;

  private TinyUrlRepository tinyUrlRepository;

  private UserRepository userRepository;

  private TinyUrlProperties tinyUrlProperties;

  @Autowired
  public TinyUrlService(
      TinyUrlRepository tinyUrlRepository,
      UserRepository userRepository,
      TinyUrlProperties tinyUrlProperties) {
    this.tinyUrlRepository = tinyUrlRepository;
    this.userRepository = userRepository;
    this.tinyUrlProperties = tinyUrlProperties;
  }

  /**
   * Creates tinyUrl from the originalUrl. If customAlias is provided, it will be used as the short
   * url key.
   *
   * @param originalUrl Url to shorten.
   * @param customAlias Optional custom alias to be used as short url key. Should be unique else
   * @param username Optional username of the owner of the tiny url.
   * @param expirationInSeconds Optional Expiration in seconds.
   * @return @{@link TinyUrl} object on success.
   * @throws @{@link IllegalArgumentException} if input validation fails such as originalUrl is
   *     empty.
   * @throws @{@link ServiceException}. Possible error codes : @{@link
   *     ErrorCode#CUSTOM_ALIAS_EXISTS} if customAlias is not unique , @{@link
   *     ErrorCode#SHORT_URL_RETRY_EXHAUSTED} if retry limit is exhausted for unique short url key
   *     generation.
   */
  public TinyUrl createTinyUrl(
      String originalUrl, String customAlias, String username, Long expirationInSeconds) {

    checkArgument(StringUtils.isNotEmpty(originalUrl), "Original url should not be empty");

    if (StringUtils.isNotEmpty(customAlias)
        && tinyUrlRepository.findByShortUrl(customAlias).isPresent()) {
      throw new ServiceException(ErrorCode.CUSTOM_ALIAS_EXISTS);
    }

    String shortUrl = StringUtils.isNotEmpty(customAlias) ? customAlias : buildShortUrl();
    UserUDT userUDT = buildUserUDT(username);

    Duration expiration =
        expirationInSeconds != null
            ? Duration.ofSeconds(expirationInSeconds)
            : Duration.ofDays(tinyUrlProperties.getDefaultUrlExpirationInDays());
    if (expiration.toDays() > tinyUrlProperties.getMaxUrlExpirationInDays()) {
      expiration = Duration.ofDays(tinyUrlProperties.getMaxUrlExpirationInDays());
    }

    Date expirationDate = Date.from(Instant.now().plus(expiration));
    TinyUrl tinyUrl =
        TinyUrl.builder()
            .originalUrl(originalUrl)
            .shortUrl(shortUrl)
            .customAlias(customAlias)
            .user(userUDT)
            .expiredAt(expirationDate)
            .createdAt(new Date())
            .build();
    return tinyUrlRepository.save(tinyUrl);
  }

  /**
   * Find tinyUrl by short url key.
   *
   * @param shortUrl unique short url key
   * @return @{@link TinyUrl} object wrapped in @{@link Optional} or empty if the short url key is
   *     not found.
   */
  public Optional<TinyUrl> getTinyUrlByShortUrl(String shortUrl) {
    checkArgument(StringUtils.isNotEmpty(shortUrl), "Short url should not be empty");
    return tinyUrlRepository.findByShortUrl(shortUrl);
  }

  private String buildShortUrl() {
    for (int i = 0; i < tinyUrlProperties.getMaxShortUrlRetry(); i++) {
      String id = UUIDs.timeBased().toString().replaceAll("-", "");
      String shortUrl = base62Encode(id);
      if (!tinyUrlRepository.existsByShortUrl(shortUrl)) {
        return shortUrl;
      }
    }
    throw new ServiceException(ErrorCode.SHORT_URL_RETRY_EXHAUSTED);
  }

  private String base62Encode(String id) {
    BigInteger uuidValue = new BigInteger(id, HEX_BASE);
    StringBuilder shortUrlBuilder = new StringBuilder();

    while (uuidValue.compareTo(BigInteger.ZERO) > 0) {
      int offset = uuidValue.mod(BASE_62).intValue();
      uuidValue = uuidValue.divide(BASE_62);
      if (offset < 26) {
        shortUrlBuilder.append((char) ('a' + offset));
      } else if (offset < 52) {
        shortUrlBuilder.append((char) ('A' + offset - 26));
      } else {
        shortUrlBuilder.append((char) ('0' + offset));
      }
    }

    return shortUrlBuilder.substring(0, tinyUrlProperties.getShortUrlLength());
  }

  private UserUDT buildUserUDT(String username) {
    Optional<User> userOptional =
        StringUtils.isNotEmpty(username)
            ? userRepository.findByUsername(username)
            : Optional.empty();
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      return UserUDT.builder()
          .username(user.getUsername())
          .email(user.getEmail())
          .lastLoginDate(user.getLastLoginDate())
          .build();
    } else {
      return null;
    }
  }
}
