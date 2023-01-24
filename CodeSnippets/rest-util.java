
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OrderGateway {

  @Autowired
  @Qualifier("gatewayRestTemplate")
  private RestTemplate gatewayRestTemplate;

  // conveniently add bread crumbs
  private <T> ResponseEntity<T> exchange(String path, HttpMethod method, HttpEntity<?> request, Class<T> responseType) {
    Monit.addHttpBreadcrumb(path, method.name(), null, request.getHeaders(), request.getBody());
    return gatewayRestTemplate.exchange(path, method, request, responseType);
  }

  public String post(final String path, final String body) {
    return post(path, body, new HashMap<>());
  }

  public String post(final String path, final String body, final Map<String, String> headers) {
    final HttpHeaders httpHeaders = HttpNetworkClient.addHeaders(headers);
    HttpEntity<String> request = body != null ? new HttpEntity<>(body, httpHeaders) : new HttpEntity<>(httpHeaders);
    try {
      ResponseEntity<String> response = exchange(path, HttpMethod.POST, request, String.class);
      return response.getBody();
    } catch (ResourceAccessException rae) {
      log.debug("Request: {} | for endpoint {} failed with exception: {}", body, path, rae.getMessage());
      throw OrderGatewayRecoverableException.of(HttpStatus.BAD_GATEWAY, rae.getMessage());
    }
  }

  public String delete(final String path, final String body, final Map<String, String> headers) {
    final HttpHeaders httpHeaders = HttpNetworkClient.addHeaders(headers);
    HttpEntity<String> request = body != null ? new HttpEntity<>(body, httpHeaders) : new HttpEntity<>(httpHeaders);
    try {
      ResponseEntity<String> response = exchange(path, HttpMethod.DELETE, request, String.class);
      return response.getBody();
    } catch (ResourceAccessException rae) {
      log.warn("Request: {} | for endpoint {} failed with exception: {}", body, path, rae.getMessage());
      throw OrderGatewayRecoverableException.of(HttpStatus.BAD_GATEWAY, rae.getMessage());
    }
  }

  public String put(final String path, final String body) {
    return this.put(path, body, new HashMap<>());
  }

  public String put(final String path, final String body, final Map<String, String> headers) {
    final HttpHeaders httpHeaders = HttpNetworkClient.addHeaders(headers);
    HttpEntity<String> request = body != null ? new HttpEntity<>(body, httpHeaders) : new HttpEntity<>(httpHeaders);
    try {
      ResponseEntity<String> response = exchange(path, HttpMethod.PUT, request, String.class);
      return response.getBody();
    } catch (ResourceAccessException rae) {
      log.warn("Request: {} | for endpoint {} failed with exception: {}", body, path, rae.getMessage());
      throw OrderGatewayRecoverableException.of(HttpStatus.BAD_GATEWAY, rae.getMessage());
    }
  }

  public String get(String url, Map<String, String> headers) {
    final HttpHeaders httpHeaders = HttpNetworkClient.addHeaders(headers);
    HttpEntity<String> request = new HttpEntity<>(httpHeaders);
    try {
      ResponseEntity<String> response = exchange(url, HttpMethod.GET, request, String.class);
      return response.getBody();
    } catch (ResourceAccessException rae) {
      log.warn("Request: for endpoint {} failed with exception: {}", url, rae.getMessage());
      throw OrderGatewayRecoverableException.of(HttpStatus.BAD_GATEWAY, rae.getMessage());
    }
  }

}


// usage

 final String responseString = orderGateway.post(microServicesApiResolver.getBseMembersUrl(), addMemberRequest.toString());
 final BseMemberDto bseMemberDto = deserializeContent(responseString, BseMemberDto.class);
// https://www.baeldung.com/jackson-object-mapper-tutorial
// https://www.baeldung.com/jackson-json-view-annotation
 private <T> T deserializeContent(final String content, final Class<T> contentClass) throws IOException {
    return objectMapper
        .readerWithView(View.Bse.class).forType(contentClass)
        .readValue(content);
  }
