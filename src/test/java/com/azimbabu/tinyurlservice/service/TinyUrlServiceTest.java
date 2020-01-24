package com.azimbabu.tinyurlservice.service;

import com.azimbabu.tinyurlservice.config.TinyUrlProperties;
import com.azimbabu.tinyurlservice.model.TinyUrl;
import com.azimbabu.tinyurlservice.model.User;
import com.azimbabu.tinyurlservice.model.UserUDT;
import com.azimbabu.tinyurlservice.repository.TinyUrlRepository;
import com.azimbabu.tinyurlservice.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TinyUrlServiceTest {

  @Mock private TinyUrlRepository tinyUrlRepository;

  @Mock private UserRepository userRepository;

  private TinyUrlProperties tinyUrlProperties;

  private TinyUrlService tinyUrlService;

  @BeforeEach
  void setUp() {
    initMocks(this);
    tinyUrlProperties = buildTinyUrlProperties();
    tinyUrlService = new TinyUrlService(tinyUrlRepository, userRepository, tinyUrlProperties);
  }

  @Test
  void nullUrlCheck() {
    assertThrows(
        IllegalArgumentException.class, () -> tinyUrlService.createTinyUrl(null, null, null, 500l));
  }

  @Test
  void emptyUrlCheck() {
    assertThrows(
        IllegalArgumentException.class, () -> tinyUrlService.createTinyUrl("", null, null, 500l));
  }

  @Test
  void customAliasCheck() {
    String customAlias = "abcd123";
    // custom alias exists
    doReturn(Optional.of(mock(TinyUrl.class)))
        .when(tinyUrlRepository)
        .findByShortUrl(eq(customAlias));
    // Expecting service exception with CUSTOM_ALIAS_EXISTS error code
    ServiceException exception =
        assertThrows(
            ServiceException.class,
            () -> tinyUrlService.createTinyUrl("http://www.test.com", customAlias, null, 500l));
    assertEquals(ErrorCode.CUSTOM_ALIAS_EXISTS, exception.getErrorCode());
  }

  @Test
  void customAliasTooLong() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            tinyUrlService.createTinyUrl(
                "http://www.test.com",
                RandomStringUtils.randomAlphabetic(tinyUrlProperties.getShortUrlLength() + 1),
                null,
                500l));
  }

  @Test
  void useCustomAlias() {
    String originalUrl = "http://www.test.com";
    String customAlias = "abcd123";

    // custom alias doesn't exist
    doReturn(Optional.empty()).when(tinyUrlRepository).findByShortUrl(eq(customAlias));

    // mock saving
    doAnswer(returnsFirstArg()).when(tinyUrlRepository).save(any(TinyUrl.class));

    TinyUrl tinyUrl = tinyUrlService.createTinyUrl(originalUrl, customAlias, null, 500l);

    assertNotNull(tinyUrl);
    assertEquals(customAlias, tinyUrl.getShortUrl());
    assertEquals(customAlias, tinyUrl.getCustomAlias());
    assertEquals(originalUrl, tinyUrl.getOriginalUrl());
    assertNotNull(tinyUrl.getCreatedAt());
    assertNotNull(tinyUrl.getExpiredAt());
    assertNull(tinyUrl.getUser());
  }

  @Test
  void buildShortUrl() {
    String originalUrl = "http://www.test.com";

    // mock saving
    doAnswer(returnsFirstArg()).when(tinyUrlRepository).save(any(TinyUrl.class));

    TinyUrl tinyUrl = tinyUrlService.createTinyUrl(originalUrl, null, null, 500l);

    assertNotNull(tinyUrl);
    assertTrue(StringUtils.isNotEmpty(tinyUrl.getShortUrl()));
    assertNull(tinyUrl.getCustomAlias());
    assertEquals(originalUrl, tinyUrl.getOriginalUrl());
    assertNotNull(tinyUrl.getCreatedAt());
    assertNotNull(tinyUrl.getExpiredAt());
    assertNull(tinyUrl.getUser());
  }

  @Test
  void saveWithUser() {
    String originalUrl = "http://www.test.com";
    String username = "testuser@gmail.com";

    // mock user
    User user = buildUser(username);
    doReturn(Optional.of(user)).when(userRepository).findByUsername(eq(username));
    // mock saving
    doAnswer(returnsFirstArg()).when(tinyUrlRepository).save(any(TinyUrl.class));

    TinyUrl tinyUrl = tinyUrlService.createTinyUrl(originalUrl, null, username, 500l);

    assertNotNull(tinyUrl);
    assertTrue(StringUtils.isNotEmpty(tinyUrl.getShortUrl()));
    assertNull(tinyUrl.getCustomAlias());
    assertEquals(originalUrl, tinyUrl.getOriginalUrl());
    assertNotNull(tinyUrl.getCreatedAt());
    assertNotNull(tinyUrl.getExpiredAt());

    UserUDT tinyUrlUser = tinyUrl.getUser();
    assertNotNull(tinyUrlUser);
    assertEquals(user.getUsername(), tinyUrlUser.getUsername());
    assertEquals(user.getEmail(), tinyUrlUser.getEmail());
    assertEquals(user.getLastLoginDate(), tinyUrlUser.getLastLoginDate());
  }

  @Test
  void useExpiration() {
    String originalUrl = "http://www.test.com";
    Instant now = Instant.now();
    int days = 7;

    // mock saving
    doAnswer(returnsFirstArg()).when(tinyUrlRepository).save(any(TinyUrl.class));

    TinyUrl tinyUrl =
        tinyUrlService.createTinyUrl(originalUrl, null, null, Duration.ofDays(days).getSeconds());

    assertNotNull(tinyUrl);
    assertTrue(StringUtils.isNotEmpty(tinyUrl.getShortUrl()));
    assertNull(tinyUrl.getCustomAlias());
    assertEquals(originalUrl, tinyUrl.getOriginalUrl());
    assertNotNull(tinyUrl.getCreatedAt());
    assertNotNull(tinyUrl.getExpiredAt());
    assertNull(tinyUrl.getUser());

    assertEquals(days, Duration.between(now, tinyUrl.getExpiredAt().toInstant()).toDays());
  }

  @Test
  void useDefaultExpiration() {
    String originalUrl = "http://www.test.com";
    Instant now = Instant.now();

    // mock saving
    doAnswer(returnsFirstArg()).when(tinyUrlRepository).save(any(TinyUrl.class));

    TinyUrl tinyUrl = tinyUrlService.createTinyUrl(originalUrl, null, null, null);

    assertNotNull(tinyUrl);
    assertTrue(StringUtils.isNotEmpty(tinyUrl.getShortUrl()));
    assertNull(tinyUrl.getCustomAlias());
    assertEquals(originalUrl, tinyUrl.getOriginalUrl());
    assertNotNull(tinyUrl.getCreatedAt());
    assertNotNull(tinyUrl.getExpiredAt());
    assertNull(tinyUrl.getUser());

    assertEquals(
        Long.valueOf(tinyUrlProperties.getDefaultUrlExpirationInDays()),
        Duration.between(now, tinyUrl.getExpiredAt().toInstant()).toDays());
  }

  @Test
  void useMaxExpiration() {
    String originalUrl = "http://www.test.com";
    Instant now = Instant.now();
    int days = 700;

    // mock saving
    doAnswer(returnsFirstArg()).when(tinyUrlRepository).save(any(TinyUrl.class));

    TinyUrl tinyUrl =
        tinyUrlService.createTinyUrl(originalUrl, null, null, Duration.ofDays(days).getSeconds());

    assertNotNull(tinyUrl);
    assertTrue(StringUtils.isNotEmpty(tinyUrl.getShortUrl()));
    assertNull(tinyUrl.getCustomAlias());
    assertEquals(originalUrl, tinyUrl.getOriginalUrl());
    assertNotNull(tinyUrl.getCreatedAt());
    assertNotNull(tinyUrl.getExpiredAt());
    assertNull(tinyUrl.getUser());

    assertEquals(
        Long.valueOf(tinyUrlProperties.getMaxUrlExpirationInDays()),
        Duration.between(now, tinyUrl.getExpiredAt().toInstant()).toDays());
  }

  @Test
  void retryBuildShortUrl() {
    String originalUrl = "http://www.test.com";

    // mock the scenario that first and second generated short urls exist and third generated short
    // url doesn't exist.
    doReturn(true)
        .doReturn(true)
        .doReturn(false)
        .when(tinyUrlRepository)
        .existsByShortUrl(anyString());

    // mock saving
    doAnswer(returnsFirstArg()).when(tinyUrlRepository).save(any(TinyUrl.class));

    TinyUrl tinyUrl = tinyUrlService.createTinyUrl(originalUrl, null, null, 500l);

    assertNotNull(tinyUrl);
    assertTrue(StringUtils.isNotEmpty(tinyUrl.getShortUrl()));
    assertNull(tinyUrl.getCustomAlias());
    assertEquals(originalUrl, tinyUrl.getOriginalUrl());
    assertNotNull(tinyUrl.getCreatedAt());
    assertNotNull(tinyUrl.getExpiredAt());
    assertNull(tinyUrl.getUser());

    // verify that existsByShortUrl is called 3 times
    verify(tinyUrlRepository, times(3)).existsByShortUrl(anyString());
  }

  @Test
  void retryBuildShortUrlExhausted() {
    String originalUrl = "http://www.test.com";

    // mock the scenario that all the retries are exhausted
    doReturn(true)
        .doReturn(true)
        .doReturn(true)
        .doReturn(true)
        .doReturn(true)
        .when(tinyUrlRepository)
        .existsByShortUrl(anyString());

    // Expecting service exception with CUSTOM_ALIAS_EXISTS error code
    ServiceException exception =
        assertThrows(
            ServiceException.class,
            () -> tinyUrlService.createTinyUrl(originalUrl, null, null, 500l));
    assertEquals(ErrorCode.SHORT_URL_RETRY_EXHAUSTED, exception.getErrorCode());

    // verify that existsByShortUrl is called 3 times
    verify(tinyUrlRepository, times(tinyUrlProperties.getMaxShortUrlRetry()))
        .existsByShortUrl(anyString());
  }

  @Test
  void getTinyUrlByNullShortUrl() {
    assertThrows(IllegalArgumentException.class, () -> tinyUrlService.getTinyUrlByShortUrl(null));
  }

  @Test
  void getTinyUrlByEmptyShortUrl() {
    assertThrows(IllegalArgumentException.class, () -> tinyUrlService.getTinyUrlByShortUrl(""));
  }

  @Test
  void getTinyUrlByShortUrlNotFound() {
    String shortUrl = "abc1234";
    doReturn(Optional.empty()).when(tinyUrlRepository).findByShortUrl(eq(shortUrl));
    assertFalse(tinyUrlService.getTinyUrlByShortUrl(shortUrl).isPresent());
  }

  @Test
  void getTinyUrlByShortUrlFound() {
    String shortUrl = "abc1234";
    doReturn(Optional.of(mock(TinyUrl.class))).when(tinyUrlRepository).findByShortUrl(eq(shortUrl));
    assertTrue(tinyUrlService.getTinyUrlByShortUrl(shortUrl).isPresent());
  }

  private User buildUser(String username) {
    return User.builder()
        .username(username)
        .email(RandomStringUtils.randomAlphabetic(5) + "@gmail.com")
        .createdAt(new Date())
        .lastLoginDate(new Date())
        .build();
  }

  private TinyUrlProperties buildTinyUrlProperties() {
    return TinyUrlProperties.builder()
        .shortUrlLength(7)
        .maxShortUrlRetry(5)
        .maxUrlExpirationInDays(365)
        .defaultUrlExpirationInDays(30)
        .build();
  }
}
