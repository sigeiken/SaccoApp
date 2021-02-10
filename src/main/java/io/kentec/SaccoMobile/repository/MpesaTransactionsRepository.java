package io.kentec.SaccoMobile.repository;

import io.kentec.SaccoMobile.entity.MpesaTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MpesaTransactionsRepository extends JpaRepository<MpesaTransactions, Long> {
    @Query("SELECT COUNT(o.id) FROM MpesaTransactions o WHERE o.mpesaTranType = :mpesaTranType AND o.processingStatus = :processingStatus AND o.checkoutRequestId = :checkoutRequestId AND o.merchantRequestId = :merchantRequestId")
    long getStkC2BTransactionCountByCheckoutRequestIdMerchantRequestId(@Param("mpesaTranType") String mpesaTranType, @Param("processingStatus") String processingStatus, @Param("checkoutRequestId") String checkoutRequestId, @Param("merchantRequestId") String merchantRequestId);

    @Query("SELECT o FROM MpesaTransactions o WHERE o.mpesaTranType = :mpesaTranType AND o.processingStatus = :processingStatus AND o.checkoutRequestId = :checkoutRequestId AND o.merchantRequestId = :merchantRequestId")
    MpesaTransactions getStkC2BTransactionByCheckoutRequestIdMerchantRequestId(@Param("mpesaTranType") String mpesaTranType, @Param("processingStatus") String processingStatus, @Param("checkoutRequestId") String checkoutRequestId, @Param("merchantRequestId") String merchantRequestId);

    @Query("SELECT o FROM MpesaTransactions o WHERE o.mpesaTranType = :mpesaTranType AND o.processingStatus = :processingStatus ")
    List<MpesaTransactions> fetchUnprocessedC2BPayments(@Param("mpesaTranType") String mpesaTranType, @Param("processingStatus") String processingStatus);

    @Query("SELECT o FROM MpesaTransactions o WHERE o.mpesaTranType = :mpesaTranType AND o.processingStatus = :processingStatus ")
    List<MpesaTransactions> fetchUnprocessedB2CPayments(@Param("mpesaTranType") String mpesaTranType, @Param("processingStatus") String processingStatus);

    @Query("SELECT COUNT(t.id) FROM MpesaTransactions t WHERE t.mpesaTranType = :mpesaTranType AND t.processingStatus = :processingStatus AND t.originatorConversationId = :originatorConversationID AND t.conversationId = :conversationID" )
    long getB2CTransactionCountByOriginatorConversationIdConversationId(@Param("mpesaTranType") String mpesaTranType, @Param("processingStatus") String processingStatus, @Param("originatorConversationID") String originatorConversationID, @Param("conversationID") String conversationID);

    @Query("SELECT t FROM MpesaTransactions t WHERE t.mpesaTranType = :mpesaTranType AND t.processingStatus = :processingStatus AND t.originatorConversationId = :originatorConversationID AND t.conversationId = :conversationID" )
    MpesaTransactions getB2CTransactionByOriginatorConversationIdConversationId(@Param("mpesaTranType") String mpesaTranType, @Param("processingStatus") String processingStatus, @Param("originatorConversationID") String originatorConversationID, @Param("conversationID") String conversationID);
}
