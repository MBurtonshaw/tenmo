package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {
    private JdbcUserDao jdbcDao;

    public AccountController(JdbcUserDao jdbcDao) {
        this.jdbcDao = jdbcDao;
    }

    // View Current Balance

    @RequestMapping(path = "/{id}/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(@PathVariable int id) {
        BigDecimal balance = jdbcDao.getBalanceById(id);
        return balance;
    }
//    @PreAuthorize("permitAll")
//    @RequestMapping(path = "/{id}/balance/add", method = RequestMethod.GET)
//    public BigDecimal addBalance(@PathVariable int id, BigDecimal amount) {
////        BigDecimal balance = jdbcDao.addToBalance(new BigDecimal(1000), id);
//        amount = new BigDecimal(1000);
//        return amount;
//    }
//    @PreAuthorize("permitAll")
//    @RequestMapping(path = "/Transfer", method = RequestMethod.POST)
//    public Transfer actualAddBalance(@RequestBody Transfer transfer) {
//        Transfer balance = jdbcDao.transact(transfer);
//        return balance;
//    }
}
