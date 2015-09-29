package com.google.auth.oauth2;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.auth.http.AuthHttpConstants;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

/**
 * Internal utilities for the com.google.auth.oauth2 namespace.
 */
class OAuth2Utils {
  static final URI TOKEN_SERVER_URI = URI.create("https://accounts.google.com/o/oauth2/token");
  static final URI TOKEN_REVOKE_URI = URI.create("https://accounts.google.com/o/oauth2/revoke");
  static final URI USER_AUTH_URI = URI.create("https://accounts.google.com/o/oauth2/auth");

  static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  static final Charset UTF_8 = Charset.forName("UTF-8");

  private static String VALUE_NOT_FOUND_MESSAGE = "%sExpected value %s not found.";
  private static String VALUE_WRONG_TYPE_MESSAGE = "%sExpected %s value %s of wrong type.";

  static final String BEARER_PREFIX = AuthHttpConstants.BEARER + " ";

  /**
   * Returns whether the headers contain the specified value as one of the entries in the
   * specified header.
   */
  static boolean headersContainValue(HttpHeaders headers, String headerName, String value) {
    Object values = headers.get(headerName);
    if (values instanceof Collection) {
      @SuppressWarnings("unchecked")
      Collection<Object> valuesCollection = (Collection<Object>) values;
      return valuesCollection.contains(value);
    }
    return false;
  }

  /**
   * Throw the exception with the specified cause.
   *
   * <p>Needed instead of constructor version to be compatible with JDK 1.5 which is required
   * until Andriod libraries can update to JDK 1.6
   */
  static <T extends Throwable> T exceptionWithCause(T exception, Throwable cause) {
    exception.initCause(cause);
    return exception;
  }

  /**
   * Parses the specified JSON text.
   */
  static GenericJson parseJson(String json) throws IOException {
    JsonObjectParser parser = new JsonObjectParser(OAuth2Utils.JSON_FACTORY);
    InputStream stateStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
    GenericJson stateJson = parser.parseAndClose(
        stateStream, StandardCharsets.UTF_8, GenericJson.class);
    return stateJson;
  }

  /**
   * Return the specified string from JSON or throw a helpful error message.
   */
  static String validateString(Map<String, Object> map, String key, String errorPrefix)
      throws IOException {
    Object value = map.get(key);
    if (value == null) {
      throw new IOException(String.format(VALUE_NOT_FOUND_MESSAGE, errorPrefix, key));
    }
    if (!(value instanceof String)) {
      throw new IOException(
          String.format(VALUE_WRONG_TYPE_MESSAGE, errorPrefix, "string", key));
    }
    return (String) value;
  }

  /**
   * Return the specified optional string from JSON or throw a helpful error message.
   */
  static String validateOptionalString(Map<String, Object> map, String key, String errorPrefix)
      throws IOException {
    Object value = map.get(key);
    if (value == null) {
      return null;
    }
    if (!(value instanceof String)) {
      throw new IOException(
          String.format(VALUE_WRONG_TYPE_MESSAGE, errorPrefix, "string", key));
    }
    return (String) value;
  }

  /**
   * Return the specified integer from JSON or throw a helpful error message.
   */
  static int validateInt32(Map<String, Object> map, String key, String errorPrefix)
      throws IOException {
    Object value = map.get(key);
    if (value == null) {
      throw new IOException(String.format(VALUE_NOT_FOUND_MESSAGE, errorPrefix, key));
    }
    if (value instanceof BigDecimal) {
      BigDecimal bigDecimalValue = (BigDecimal) value;
      return bigDecimalValue.intValueExact();
    }
    if (!(value instanceof Integer)) {
      throw new IOException(
          String.format(VALUE_WRONG_TYPE_MESSAGE, errorPrefix, "integer", key));
    }
    return (Integer) value;
  }

  /**
   * Return the specified long from JSON or throw a helpful error message.
   */
  static long validateLong(Map<String, Object> map, String key, String errorPrefix)
      throws IOException {
    Object value = map.get(key);
    if (value == null) {
      throw new IOException(String.format(VALUE_NOT_FOUND_MESSAGE, errorPrefix, key));
    }
    if (value instanceof BigDecimal) {
      BigDecimal bigDecimalValue = (BigDecimal) value;
      return bigDecimalValue.longValueExact();
    }
    if (!(value instanceof Long)) {
      throw new IOException(
          String.format(VALUE_WRONG_TYPE_MESSAGE, errorPrefix, "long", key));
    }
    return (Long) value;
  }

  private OAuth2Utils() {
  }
}
