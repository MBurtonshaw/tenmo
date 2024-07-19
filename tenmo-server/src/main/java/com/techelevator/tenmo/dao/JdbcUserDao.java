package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.RegisterUserDto;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
    private final JdbcTemplate jdbcTemplate;
    private final Account account = new Account();

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUserById(int userId) {
        User user = null;
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                user = mapRowToUser(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                User user = mapRowToUser(results);
                users.add(user);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return users;
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null) throw new IllegalArgumentException("Username cannot be null");
        User user = null;
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username = LOWER(TRIM(?));";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
            if (rowSet.next()) {
                user = mapRowToUser(rowSet);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return user;
    }

    @Override
    public User createUser(RegisterUserDto user) {
        User newUser = null;
        // create user
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (LOWER(TRIM(?)), ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(user.getPassword());
        try {
            int newUserId = jdbcTemplate.queryForObject(sql, int.class, user.getUsername(), password_hash);
            newUser = getUserById(newUserId);
            if (newUser != null) {
                // create account
                sql = "INSERT INTO account (user_id, balance) VALUES (?, ?)";
                jdbcTemplate.update(sql, newUserId, STARTING_BALANCE);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return newUser;
    }

    @Override
    public BigDecimal getBalance(int userId) {
        return null;
    }

    @Transactional
    @Override
    public Transfer transact(Transfer transfer) {
        Transfer transaction = null;
        BigDecimal updatedAmount = null;
        String sql = "UPDATE account SET balance = balance - ? WHERE user_id = ?;";
        String sql2 = "INSERT INTO transfer(transfer_type_id, transfer_status_id, account_from, account_to, amount)" +
                "VALUES(?, ?, ?, ?, ?) RETURNING transfer_id;";
        int transferId = jdbcTemplate.queryForObject(sql2, int.class, transaction.getAmount(), transaction.getAccount_from());

        if(transferId > 0) {
            boolean success = true;
            if (transaction.getAmount().intValue() <= 0 && (transaction.getAccount_from() == transaction.getAccount_to())) {


                success = false;
            } else {
//                SqlRowSet trans = jdbcTemplate.queryForRowSet(sql2, transaction.getTransfer_type_id(), transaction.getTransfer_status_id(),
//                        transaction.getAccount_from(), transaction.getAccount_to(), transaction.getAmount());
                try {
                    int numberOfRows = jdbcTemplate.update(sql, transaction.getAmount(), transaction.getAccount_from());
                    int numberOfRows2 = jdbcTemplate.update(sql, transaction.getAmount().multiply(new BigDecimal(-1)),
                                                            transaction.getAccount_to());
                } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
                updatedAmount = getBalanceById(transaction.getAccount_from());
            }
        }
        // Find from account and to account
        //Validate if from account has enough money and is not transferring <=0
        //Take money from the From_account
        //Update database From_account
        //add momey to To_account
        //Update database To_account
        //in data CREATE transfer object Return the transfer_id
//        try {
//
//        } catch (CannotGetJdbcConnectionException e) {
//            throw new DaoException("Unable to connect to server or database", e);
//        } catch (DataIntegrityViolationException e) {
//            throw new DaoException("Data integrity violation", e);
//        }


        return transaction;

    }

    @Override
    public BigDecimal subtractFromBalance(BigDecimal amountToSubtract, int id) {
        return null;
    }

    public BigDecimal getBalanceById(int userId) {
        BigDecimal balance = null;
        String sql = "SELECT balance " +
                        "FROM account " +
                        "WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        //

        if (results.next()) {
            //returns to CLI
            try {
                // balance = mapRowToUser(results);
                balance = results.getBigDecimal("balance");

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return balance;
    }


    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }
}
