package com.transfer.controller.model;

import com.transfer.service.model.TransactionInfo;
import lombok.Data;

import java.util.List;

@Data
public class TransactionListRes {

    private final List<TransactionInfo> list;
}