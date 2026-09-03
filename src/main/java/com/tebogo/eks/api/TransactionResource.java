package com.tebogo.eks.api;

import com.tebogo.eks.bootstrap.ApplicationContext;
import com.tebogo.eks.dto.TransactionRequest;
import com.tebogo.eks.model.PaymentTransaction;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v1/transactions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @POST
    public Response create(
            TransactionRequest request) {

        PaymentTransaction transaction =
                ApplicationContext
                        .transactionService()
                        .create(request);

        return Response
                .status(Response.Status.CREATED)
                .entity(transaction)
                .build();
    }
}