package com.transfer.controller;

import com.transfer.controller.model.ErrorCode;
import com.transfer.controller.model.ErrorResponse;
import com.transfer.controller.model.ExecutionException;
import com.transfer.controller.model.InvalidRequestException;
import com.transfer.executor.FutureDSL;
import com.transfer.service.transformer.Transformer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Function;

@Slf4j
@AllArgsConstructor
abstract class AbstractController extends HttpServlet {

    protected final Transformer transformer;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        throw new ExecutionException(ErrorCode.INVALID_REQUEST, "Unsupported operation");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        throw new ExecutionException(ErrorCode.INVALID_REQUEST, "Unsupported operation");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        throw new ExecutionException(ErrorCode.INVALID_REQUEST, "Unsupported operation");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        throw new ExecutionException(ErrorCode.INVALID_REQUEST, "Unsupported operation");
    }

    <T, R> void process(HttpServletRequest req,
                        HttpServletResponse resp,
                        Function<HttpServletRequest, T> prepareReqFun,
                        Function<? super T, FutureDSL<? extends R>> executeFun) {
        AsyncContext asyncContext = req.startAsync();
        FutureDSL.fromTask(() -> prepareReqFun.apply(req))
                .thenCompose(executeFun::apply)
                .thenApply(result -> {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    return transformer.toJson(result);
                })
                .exceptionally(t -> {
                    ErrorResponse errorResponse;
                    if (t instanceof InvalidRequestException) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        InvalidRequestException ex = (InvalidRequestException) t;
                        errorResponse = new ErrorResponse(ex.getErrorCode().getCode(), ex.getMessage());
                    } else if (t instanceof ExecutionException) {
                        ExecutionException ex = (ExecutionException) t;
                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        errorResponse = new ErrorResponse(ex.getErrorCode().getCode(), ex.getMessage());
                    } else {
                        errorResponse = new ErrorResponse(ErrorCode.UNKNOWN_ERROR.getCode(), t.getMessage());
                    }
                    return transformer.toJson(errorResponse);
                })
                .thenAccept(json -> {
                    resp.setContentType("application/json");
                    try {
                        resp.getWriter().println(json);
                    } catch (IOException e) {
                        log.error("Unable to write response", e);
                    }
                    asyncContext.complete();
                });
    }
}