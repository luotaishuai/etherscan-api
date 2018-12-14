package com.test.etherscan.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.web3j.utils.Convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anonymity
 * @create 2018-12-14 18:46
 **/
@Slf4j
public class EtherscanApiHelper {

    private static final String API_URL = "https://api-ropsten.etherscan.io/api";
    private static final String API_KEY = "B6HZQ7IUEG4QUAS6UX5AD99V8BISJRDJYN";
    private static Map<String, Object> params;

    public static void main(String[] args) {
        String balance = getBalance("0xcd897165D5f7F3D75D10070712772c50F8B377aE");
        System.err.println(balance);
        Object o = getTransactions("0xcd897165D5f7F3D75D10070712772c50F8B377aE", 1, 10, null, null, null);
        System.err.println(o);

    }

    private static Object getTransactions(String address, Integer page, Integer offset, Integer startblock, Integer endblock, String sort){
        ReqParams reqParams = new ReqParams(page, offset, startblock, endblock, sort);
        initParams();
        addParams("module", "account");
        addParams("action", "txlist");
        addParams("address", address);
        addParams("startblock", reqParams.getStartblock());
        addParams("endblock", reqParams.getEndblock());
        addParams("page", reqParams.getPage());
        addParams("offset", reqParams.getOffset());
        addParams("sort", reqParams.getSort());

        String url = linkURL(params);
        Object result = sendGet(url);
        if (null != result){
            return JSON.parseArray(result.toString(), TransactionVo.class);
        }
        return null;
    }

    private static String getBalance(String address) {
        initParams();
        addParams("module", "account");
        addParams("action", "balance");
        addParams("tag", "latest");
        addParams("address", address);

        String url = linkURL(params);
        Object result = sendGet(url);
        if (null != result) {
            return Convert.fromWei(result.toString(), Convert.Unit.ETHER).toPlainString();
        }
        return null;
    }

    private static Object sendGet(String url) {
        String json = OkHttpClientHelper.get(url, null);
        JSONObject jsonObject = JSON.parseObject(json);
        int status = jsonObject.getInteger("status");
        if (status == 1) {
            return jsonObject.get("result");
        }
        return null;
    }

    private static String linkURL(Map<String, Object> params) {
        if (params.isEmpty()) {
            return "";
        }
        String paramsLink = "";
        List<String> keys = new ArrayList<>(params.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key).toString();
            if ("".equals(paramsLink)) {
                paramsLink += "?" + key + "=" + value;
            } else {
                paramsLink += "&" + key + "=" + value;
            }
        }
        return API_URL + paramsLink;
    }

    private static void initParams() {
        params = new HashMap<>();
        if (!params.isEmpty()) {
            params.clear();
        }
        addParams("apiKey", API_KEY);
    }

    private static void addParams(String key, Object value) {
        if (null != value) {
            params.put(key, value);
        }

    }

    @Data
    public static class ReqParams {
        private Integer page;
        private Integer offset;
        private Integer startblock;
        private Integer endblock;
        private String sort;

        public ReqParams() {
        }

        public ReqParams(Integer page, Integer offset, Integer startblock, Integer endblock, String sort) {
            this.page = (null == page || page < 1) ? 1 : page;
            this.offset = (null == offset || offset > 10) ? 20 : offset;
            this.startblock = (null == startblock || startblock < 0) ? 0 : startblock;
            this.endblock = (null == endblock || endblock > 99999999) ? 99999999 : endblock;
            this.sort = null == sort ? "asc" : sort;
        }
    }

    @Data
    public static class TransactionVo {
        private String blockHash;
        private String contractAddress;
        private String transactionIndex;
        private String confirmations;
        private String nonce;
        private String timeStamp;
        private String input;
        private String gasUsed;
        private String isError;
        private String txreceipt_status;
        private String blockNumber;
        private String gas;
        private String cumulativeGasUsed;
        private String from;
        private String to;
        private String value;
        private String hash;
        private String gasPrice;
    }

}
