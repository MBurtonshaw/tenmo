package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@PreAuthorize("permitAll")
public class TransactionController extends Transfer {
    private JdbcUserDao jdbcDao;

    public TransactionController(JdbcUserDao jdbcDao) {
        this.jdbcDao = jdbcDao;
    }

    @RequestMapping(path = "/{id}/transfer", method = RequestMethod.GET)
    public Transfer getTransfer(@RequestBody Transfer transfer) {
        Transfer balance = jdbcDao.transact(transfer);
        return null;
    }
    @RequestMapping(path = "/{id}/transfer", method = RequestMethod.POST)
    public Transfer actualAddBalance(@RequestBody Transfer transfer) {
        Transfer balance = jdbcDao.transact(transfer);
        return balance;
    }
}
