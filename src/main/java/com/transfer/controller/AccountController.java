package com.transfer.controller;

import com.transfer.controller.model.CreateAccountReq;
import com.transfer.controller.model.ErrorCode;
import com.transfer.controller.model.ExecutionException;
import com.transfer.service.AccountService;
import com.transfer.service.transformer.Transformer;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class AccountController extends AbstractController {

    private final AccountService accountService;

    public AccountController(Transformer transformer, AccountService accountService) {
        super(transformer);
        this.accountService = accountService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, this::prepareGetByIdReq, accountService::getAccountById);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, this::prepareCreateAccRequest, accountService::createAccount);
    }

    private Long prepareGetByIdReq(HttpServletRequest req) {
        return Optional.ofNullable(req.getParameter("id"))
                .map(Long::valueOf)
                .orElse(null);
    }

    private CreateAccountReq prepareCreateAccRequest(HttpServletRequest req)  {
        try {
            return transformer.fromJson(req.getReader(), CreateAccountReq.class);
        } catch (IOException e) {
            log.error("Unable to parse request", e);
            throw new ExecutionException(ErrorCode.INVALID_REQUEST, "Unable to parse request");
        }
    }
}