package com.g7.framework;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.cloudauth.model.v20180916.GetMaterialsRequest;
import com.aliyuncs.cloudauth.model.v20180916.GetMaterialsResponse;
import com.aliyuncs.cloudauth.model.v20180916.GetStatusRequest;
import com.aliyuncs.cloudauth.model.v20180916.GetStatusResponse;
import com.aliyuncs.cloudauth.model.v20180916.GetVerifyTokenRequest;
import com.aliyuncs.cloudauth.model.v20180916.GetVerifyTokenResponse;
import com.aliyuncs.cloudauth.model.v20190307.DescribeVerifyResultRequest;
import com.aliyuncs.cloudauth.model.v20190307.DescribeVerifyResultResponse;
import com.aliyuncs.cloudauth.model.v20190307.DescribeVerifyTokenRequest;
import com.aliyuncs.cloudauth.model.v20190307.DescribeVerifyTokenResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.google.gson.JsonObject;

/**
 * @author dreamyao
 * @title
 * @date 2019-06-09 23:06
 * @since 1.0.0
 */
public class CloudAuthService {

    private IAcsClient iAcsClient;
    private CloudAuthProperties cloudAuthProperties;

    public CloudAuthService(IAcsClient iAcsClient, CloudAuthProperties cloudAuthProperties) {
        this.iAcsClient = iAcsClient;
        this.cloudAuthProperties = cloudAuthProperties;
    }

    /**
     * 提交认证资料并获取认证跳转地址
     *
     * @param name                 姓名
     * @param identificationNumber 身份证号
     * @param ticketId             唯一ID
     * @return 认证 H5 跳转地址
     */
    public String submitAuth(String name, String identificationNumber, String ticketId) {

        GetVerifyTokenRequest getVerifyTokenRequest = new GetVerifyTokenRequest();
        getVerifyTokenRequest.setBiz(cloudAuthProperties.getBiz());

        getVerifyTokenRequest.setTicketId(ticketId);
        getVerifyTokenRequest.setMethod(MethodType.POST);
        //通过binding参数传入业务已经采集的认证资料，其中姓名、身份证号为必要字段
        // 若需要binding图片资料，请控制单张图片大小在 2M 内，避免拉取超时

        JsonObject object = new JsonObject();
        object.addProperty("Name", name);
        object.addProperty("IdentificationNumber", identificationNumber);

        getVerifyTokenRequest.setBinding(object.toString());
        try {
            GetVerifyTokenResponse response = iAcsClient.getAcsResponse(getVerifyTokenRequest);
            return response.getData().getCloudauthPageUrl();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取认证结果
     *
     * @param ticketId 提交认证时的唯一ID
     * @return 是否认证通过
     */
    public GetStatusResponse authResult(String ticketId) {
        GetStatusRequest getStatusRequest = new GetStatusRequest();
        getStatusRequest.setBiz(cloudAuthProperties.getBiz());
        getStatusRequest.setTicketId(ticketId);
        try {
            return iAcsClient.getAcsResponse(getStatusRequest);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    public GetMaterialsResponse getAuthMessage(String ticketId) {
        GetMaterialsRequest getMaterialsRequest = new GetMaterialsRequest();
        getMaterialsRequest.setBiz(cloudAuthProperties.getBiz());
        getMaterialsRequest.setTicketId(ticketId);
        try {
            return iAcsClient.getAcsResponse(getMaterialsRequest);
            //后续业务处理
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 为APP获取识别token
     *
     * @param bizId        认证ID
     * @param name         姓名
     * @param IdCardNumber 身份证号
     */
    public String getAppAuthToken(String bizId, String name, String IdCardNumber) {

        //1. 接入方服务端发起认证请求，获得认证token，接口文档：https://help.aliyun.com/document_detail/127470.html
        DescribeVerifyTokenRequest request = new DescribeVerifyTokenRequest();
        request.setSysRegionId(cloudAuthProperties.getRegion());
        request.setSysProtocol(ProtocolType.HTTPS);
        request.setActionName("DescribeVerifyToken");

        request.setBizId(bizId);
        request.setName(name);
        request.setIdCardNumber(IdCardNumber);
        request.setBizType(
                cloudAuthProperties.getAppBizType()); //创建方法请参见https://help.aliyun.com/document_detail/127885.html

        DescribeVerifyTokenResponse response = null;
        try {
            response = iAcsClient.getAcsResponse(request);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
        return response.getVerifyToken();
    }

    /**
     * APP查询认证结果
     *
     * @param bizId
     * @return
     */
    public DescribeVerifyResultResponse appAuthResult(String bizId) {

        //2. 接入方服务端将token传递给接入方无线客户
        //3. 接入方无线客户端用token调起无线认证SDK
        //4. 用户按照由无线认证SDK组织的认证流程页面的指引，提交认证资料
        //5. 认证流程结束退出无线认证SDK，进入客户端回调函数
        //6. 接入方服务端获取认证状态和认证资料（注：客户端无线认证SDK回调中也会携带认证状态, 但建议以服务端调接口获取的为准进行业务上的判断和处理）
        //RPBasic、RPManual方案查询认证结果接口文档：https://help.aliyun.com/document_detail/127469.html
        //FDBioOnly方案查询认证结果接口文档：https://help.aliyun.com/document_detail/127731.html
        DescribeVerifyResultRequest verifyResultRequest = new DescribeVerifyResultRequest();
        verifyResultRequest.setSysRegionId(cloudAuthProperties.getRegion());
        verifyResultRequest.setSysProtocol(ProtocolType.HTTPS);
        verifyResultRequest.setActionName("DescribeVerifyResult");

        verifyResultRequest.setBizId(bizId);
        verifyResultRequest.setBizType(cloudAuthProperties.getAppBizType());
        try {
            return iAcsClient.getAcsResponse(verifyResultRequest);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }
}
