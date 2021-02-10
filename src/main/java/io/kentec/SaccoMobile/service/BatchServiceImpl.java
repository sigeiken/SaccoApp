package io.kentec.SaccoMobile.service;

import io.kentec.SaccoMobile.entity.MpesaTransactions;
import io.kentec.SaccoMobile.repository.MpesaTransactionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchServiceImpl implements BatchService{

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MpesaService mpesaService;

    @Autowired
    private MpesaTransactionsRepository mpesaTransactionsRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchServiceImpl.class);

    @Scheduled(fixedDelay =3000)
    @Override
    public void processPendingC2BTransactions() {
        try {
            List<MpesaTransactions> pendingC2BTransactions = mpesaTransactionsRepository.fetchUnprocessedC2BPayments("C2B", "PENDING");
            LOGGER.info("{} pending C2B transaction(s)", pendingC2BTransactions.size());
            for (MpesaTransactions tran : pendingC2BTransactions) {
                try {
                    if (tran.getBillReference().startsWith("L")) {

                        transactionService.processLoanRepayment(tran.getMsisdn(), tran.getAmount().toString(), tran.getMpesaReceipt(), tran.getBillReference());
                    } else {
                        transactionService.processSavingsDeposit(tran.getMsisdn(), tran.getAmount().toString(), tran.getMpesaReceipt(), tran.getBillReference());
                    }
                    tran.setProcessingStatus("COMPLETED_OK");
                    mpesaTransactionsRepository.save(tran);
                } catch (Exception e) {
                    LOGGER.error("C2B process fail {} will retry", tran, e);
                }

            }
        } catch (Exception e) {
            LOGGER.error("C2B  batch process fail {} will retry", e);
        }
    }

    @Scheduled(fixedDelay =3000)
    @Override
    public void processPendingB2CTransactions() {
        try {
            List<MpesaTransactions> pendingB2CTransactions = mpesaTransactionsRepository.fetchUnprocessedB2CPayments("B2C", "PROCESSED");
            LOGGER.info("{} pending B2C transaction(s)", pendingB2CTransactions.size());
            for (MpesaTransactions tran : pendingB2CTransactions) {
                try {
                    mpesaService.initiateB2CPayment(tran.getMsisdn(), tran.getAmount().toString());
//                    tran.setProcessingStatus("SEND TO M-PESA");
                    mpesaTransactionsRepository.save(tran);
                } catch (Exception e) {
                    LOGGER.error("C2B process fail {} will retry", tran, e);
                }

            }
        }catch (Exception e){
            LOGGER.error("B2C  batch process fail {} will retry", e);
        }
    }
}
