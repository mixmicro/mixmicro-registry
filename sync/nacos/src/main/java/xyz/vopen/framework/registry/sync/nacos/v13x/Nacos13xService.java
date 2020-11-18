package xyz.vopen.framework.registry.sync.nacos.v13x;

import org.springframework.lang.NonNull;
import retrofit2.http.*;
import xyz.vopen.framework.registry.sync.nacos.NacosSyncException;
import xyz.vopen.framework.registry.sync.nacos.model.Namespace;
import xyz.vopen.framework.registry.sync.nacos.model.response.InstanceResponse;
import xyz.vopen.framework.registry.sync.nacos.model.response.Response;
import xyz.vopen.framework.registry.sync.nacos.model.response.ServiceResponse;
import xyz.vopen.framework.registry.sync.nacos.v13x.model.AccessToken;
import xyz.vopen.mixmicro.components.boot.httpclient.MixHttpClientLogStrategy;
import xyz.vopen.mixmicro.components.boot.httpclient.annotation.MixHttpClient;

import java.util.List;

import static xyz.vopen.framework.registry.sync.nacos.NacosConstants.DEST_NACOS_CONSOLE_ADDR_KEY;

/**
 * {@link Nacos13xService}
 *
 * <p>Class NacosService Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/17
 */
@MixHttpClient(
    baseUrl = DEST_NACOS_CONSOLE_ADDR_KEY,
    logStrategy = MixHttpClientLogStrategy.BASIC)
public interface Nacos13xService {

  // ~~ Default Constants Fields .

  int DEFAULT_PAGE_NO = 1;

  int DEFAULT_PAGE_SIZE = 1000;

  String DEFAULT_GROUP_NAME = "DEFAULT_GROUP";

  String DEFAULT_CLUSTER_NAME = "DEFAULT";

  String DEFAULT_NAMESPACE = "public";

  // ~~ Service Api Defined .

  /**
   * Nacos Service Login Api
   *
   * @param username nacos console username
   * @param password nacos console password
   * @return login result with access token
   * @throws NacosSyncException maybe thrown {@link NacosSyncException}
   */
  @FormUrlEncoded
  @POST("/nacos/v1/auth/users/login")
  AccessToken login(@Field("username") String username, @Field("password") String password) throws NacosSyncException;

  /**
   * Fetch Nacos Server Save All Namespace List
   *
   * @param accessToken auth token value
   * @return {@link Namespace} list
   * @throws NacosSyncException maybe thrown {@link NacosSyncException}
   */
  default Response<List<Namespace>> namespaces(@NonNull AccessToken accessToken) throws NacosSyncException {
    return this.namespaces(accessToken.toString(), accessToken.getAccessToken());
  }

  /**
   * Fetch Nacos Server Save All Namespace List
   *
   * @param authorization auth token value
   * @param accessToken access token
   * @return {@link Namespace} list
   * @throws NacosSyncException maybe thrown {@link NacosSyncException}
   */
  default Response<List<Namespace>> namespaces(String authorization, String accessToken) throws NacosSyncException {
    return this.namespaces(authorization, accessToken, "");
  }

  /**
   * Fetch Nacos Server Save All Namespace List
   *
   * @param authorization auth token value
   * @param namespaceId nacos console login with default namespace-id
   * @return {@link Namespace} list
   * @throws NacosSyncException maybe thrown {@link NacosSyncException}
   */
  @GET("/nacos/v1/console/namespaces")
  Response<List<Namespace>> namespaces(@Header("Authorization") String authorization, @Query("accessToken") String accessToken, @Query("namespaceId") String namespaceId) throws NacosSyncException;

  /**
   * Get a list of all services registered in a public namespace .
   *
   * @param accessToken access token
   * @return list instance of {@link ServiceResponse}
   * @throws NacosSyncException maybe thrown {@link NacosSyncException}
   */
  default ServiceResponse services(@NonNull AccessToken accessToken) throws NacosSyncException {
    return this.services(accessToken.toString(), accessToken.getAccessToken(), DEFAULT_NAMESPACE, "");
  }

  /**
   * Get a list of all services registered in a public namespace .
   *
   * @param authorization auth token value
   * @param accessToken access token
   * @return list instance of {@link ServiceResponse}
   * @throws NacosSyncException maybe thrown {@link NacosSyncException}
   */
  default ServiceResponse services(String authorization, String accessToken) throws NacosSyncException {
    return this.services(authorization, accessToken, DEFAULT_NAMESPACE, "");
  }

  /**
   * Get a list of all services registered in a given namespace.
   *
   * @param authorization auth token value
   * @param accessToken access token
   * @param namespaceId given target namespace
   * @return list instance of {@link ServiceResponse}
   * @throws NacosSyncException maybe thrown {@link NacosSyncException}
   */
  default ServiceResponse services(String authorization, String accessToken, String namespaceId) throws NacosSyncException {
    return this.services(authorization, accessToken, namespaceId, "", DEFAULT_GROUP_NAME);
  }

  /**
   * Get a list of all services registered in a given namespace.
   *
   * @param authorization auth token value
   * @param accessToken access token
   * @param namespaceId given target namespace
   * @param serviceNameParam search keyword of service name
   * @return list instance of {@link ServiceResponse}
   * @throws NacosSyncException maybe thrown {@link NacosSyncException}
   */
  default ServiceResponse services(String authorization, String accessToken, String namespaceId, String serviceNameParam) throws NacosSyncException {
    return this.services(authorization, accessToken, namespaceId, serviceNameParam, DEFAULT_GROUP_NAME);
  }

  /**
   * Get a list of all services registered in a given namespace.
   *
   * @param authorization auth token value
   * @param accessToken access token
   * @param namespaceId given target namespace
   * @param serviceNameParam search keyword of service name
   * @param groupNameParam group name
   * @return list instance of {@link ServiceResponse}
   * @throws NacosSyncException maybe thrown {@link NacosSyncException}
   */
  default ServiceResponse services(String authorization, String accessToken, String namespaceId, String serviceNameParam, String groupNameParam) throws NacosSyncException {
    return this.services(authorization, accessToken, true, DEFAULT_PAGE_NO, DEFAULT_PAGE_SIZE, namespaceId, serviceNameParam, groupNameParam);
  }

  /**
   * Get a list of all services registered in a given namespace.
   *
   * @param authorization auth token value
   * @param accessToken access token
   * @param hasIpCount whether to hide empty service records
   * @param pageNo list of page num
   * @param pageSize list page size
   * @param namespaceId given target namespace
   * @param serviceNameParam search keyword of service name
   * @param groupNameParam group name
   * @return list instance of {@link ServiceResponse}
   * @throws NacosSyncException maybe thrown {@link NacosSyncException}
   */
  @GET("/nacos/v1/ns/catalog/services?withInstances=false")
  ServiceResponse services(@Header("Authorization") String authorization, @Query("accessToken") String accessToken, @Query("hasIpCount") boolean hasIpCount, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("namespaceId") String namespaceId, @Query("serviceNameParam") String serviceNameParam, @Query("groupNameParam") String groupNameParam) throws NacosSyncException;

  /**
   * Get the list of available nodes for a given service
   *
   * @param accessToken access token
   * @param namespaceId given target namespace
   * @param serviceName service name
   * @return list instance of {@link ServiceResponse}
   * @throws NacosSyncException maybe thrown {@link NacosSyncException}
   */
  default InstanceResponse instances(AccessToken accessToken, String serviceName, String namespaceId) throws NacosSyncException {
    return this.instances(accessToken.toString(), accessToken.getAccessToken(), serviceName, DEFAULT_CLUSTER_NAME, DEFAULT_GROUP_NAME, namespaceId);
  }

  /**
   * Get the list of available nodes for a given service
   *
   * @param authorization auth token value
   * @param accessToken access token
   * @param namespaceId given target namespace
   * @param serviceName service name
   * @return list instance of {@link ServiceResponse}
   * @throws NacosSyncException maybe thrown {@link NacosSyncException}
   */
  default InstanceResponse instances(String authorization, String accessToken, String serviceName, String namespaceId) throws NacosSyncException {
    return this.instances(authorization, accessToken, serviceName, DEFAULT_CLUSTER_NAME, DEFAULT_GROUP_NAME, namespaceId);
  }

  /**
   * Get the list of available nodes for a given service
   *
   * @param authorization auth token value
   * @param accessToken access token
   * @param namespaceId given target namespace
   * @param clusterName cluster name , default value : <code>DEFAULT</code>
   * @param groupName group name
   * @param serviceName service name
   * @return list instance of {@link ServiceResponse}
   * @throws NacosSyncException maybe thrown {@link NacosSyncException}
   */
  default InstanceResponse instances(String authorization, String accessToken, String serviceName, String clusterName, String groupName, String namespaceId) throws NacosSyncException {
    return this.instances(authorization, accessToken, serviceName, clusterName, groupName, DEFAULT_PAGE_NO, DEFAULT_PAGE_SIZE, namespaceId);
  }

  /**
   * Get the list of available nodes for a given service
   *
   * @param authorization auth token value
   * @param accessToken access token
   * @param pageNo list of page num
   * @param pageSize list page size
   * @param namespaceId given target namespace
   * @param clusterName cluster name , default value : <code>DEFAULT</code>
   * @param groupName group name
   * @param serviceName service name
   * @return list instance of {@link ServiceResponse}
   * @throws NacosSyncException maybe thrown {@link NacosSyncException}
   */
  @GET("/nacos/v1/ns/catalog/instances")
  InstanceResponse instances(@Header("Authorization") String authorization, @Query("accessToken") String accessToken, @Query("serviceName") String serviceName, @Query("clusterName") String clusterName, @Query("groupName") String groupName, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("namespaceId") String namespaceId) throws NacosSyncException;

}
