package com.transfer.executor.mdc;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

public class MdcUtil {
    private static final String REQUEST_ID_KEY = "requestId";

    private MdcUtil() {
    }

    public static void setupRequestId(String requestId) {
        String trimmed;
        if(requestId != null && (trimmed = StringUtils.trimToNull(requestId)) != null) {
            MDC.put(REQUEST_ID_KEY, trimmed);
        } else {
            MDC.remove(REQUEST_ID_KEY);
        }
    }

    static String getRequestIdOrNull() {
        return MDC.get(REQUEST_ID_KEY);
    }

    static void clear() {
        MDC.clear();
    }
}