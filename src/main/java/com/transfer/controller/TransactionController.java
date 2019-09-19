package com.transfer.controller;

import com.transfer.controller.model.CreateTransactionReq;
import com.transfer.controller.model.ErrorCode;
import com.transfer.controller.model.ExecutionException;
import com.transfer.controller.model.TransactionListReq;
import com.transfer.service.TransactionService;
import com.transfer.service.transformer.Transformer;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class TransactionController extends AbstractController {

    private final TransactionService transactionService;

    public TransactionController(Transformer transformer, TransactionService transactionService) {
        super(transformer);
        this.transactionService = transactionService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, this::prepareGetListRequest, transactionService::getTransactionList);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, this::prepareCreateRequest, transactionService::createTransaction);
    }

    private TransactionListReq prepareGetListRequest(HttpServletRequest req) {
        return TransactionListReq.builder()
                .account(Optional.ofNullable(req.getParameter("account"))
                        .map(Long::valueOf)
                        .orElse(null))
                .limit(Optional.ofNullable(req.getParameter("limit"))
                        .map(Integer::valueOf)
                        .orElse(null))
                .build();
    }

    private CreateTransactionReq prepareCreateRequest(HttpServletRequest req) {
        try {
            return transformer.fromJson(req.getReader(), CreateTransactionReq.class);
        } catch (IOException e) {
            log.error("Unable to parse request", e);
            throw new ExecutionException(ErrorCode.INVALID_REQUEST, "Unable to parse request");
        }
    }
}