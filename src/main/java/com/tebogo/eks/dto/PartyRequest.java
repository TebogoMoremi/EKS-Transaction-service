package com.tebogo.eks.dto;

public record PartyRequest(
    String name,
    String accountReference,
    String channel,
    String destination
) {
}