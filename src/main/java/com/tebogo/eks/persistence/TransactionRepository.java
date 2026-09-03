package com.tebogo.eks.persistence;

import com.tebogo.eks.model.PaymentTransaction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class TransactionRepository {

    private final EntityManagerFactory entityManagerFactory;

    public TransactionRepository(
            EntityManagerFactory entityManagerFactory) {

        this.entityManagerFactory =
                entityManagerFactory;
    }

    public PaymentTransaction save(
            PaymentTransaction transaction) {

        EntityManager entityManager =
                entityManagerFactory
                        .createEntityManager();

        EntityTransaction databaseTransaction =
                entityManager.getTransaction();

        try {

            databaseTransaction.begin();

            entityManager.persist(
                    transaction
            );

            databaseTransaction.commit();

            return transaction;

        } catch (RuntimeException exception) {

            if (databaseTransaction.isActive()) {
                databaseTransaction.rollback();
            }

            throw exception;

        } finally {

            entityManager.close();
        }
    }


    public PaymentTransaction findById(
            Long id) {

        EntityManager entityManager =
                entityManagerFactory
                        .createEntityManager();

        try {

            return entityManager.find(
                    PaymentTransaction.class,
                    id
            );

        } finally {

            entityManager.close();
        }
    }
}