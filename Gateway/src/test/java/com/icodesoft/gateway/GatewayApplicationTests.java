package com.icodesoft.gateway;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@SpringBootTest
class GatewayApplicationTests {

    @Test
    void contextLoads() {

    }

    public static void  main(String[] args) throws UnsupportedEncodingException {
        String authUrl = "https://localhost:8443/authService";
        String authUrlExt = "https://dc-test-repo1.eng.vmware.com:8443/authService";
        System.out.println("===========" + authUrlExt + authUrl.substring(authUrl.length()));
        int index = StringUtils.indexOfIgnoreCase("DCPN ID : VMware-joe-dl", "VMware-joe-dl");
        System.out.println("************" + index);
        String sourceText = URLEncoder.encode("Find SDKs & APIs", "UTF-8").replaceAll(
                "\\+", "%20");
        System.out.println("**********Find SDKs & APIs: " + sourceText);

    }
}
