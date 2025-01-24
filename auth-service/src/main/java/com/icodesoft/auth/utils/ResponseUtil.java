package com.icodesoft.auth.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.icodesoft.auth.model.ResponseModel;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ResponseUtil {

    public static void write(HttpServletResponse response, ResponseModel model) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        JsonNode jsonNode = Json.toJson(model);
        String json = Json.stringify(jsonNode);
        writer.write(json);
    }
}
