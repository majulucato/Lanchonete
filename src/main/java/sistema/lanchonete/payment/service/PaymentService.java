package sistema.lanchonete.payment.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import sistema.lanchonete.client.domain.Client;
import sistema.lanchonete.client.service.ClientService;
import sistema.lanchonete.payment.domain.Payment;
import sistema.lanchonete.payment.dto.PaymentPostRequestBody;
import sistema.lanchonete.payment.dto.PaymentPutRequestBody;
import sistema.lanchonete.payment.repository.PaymentRepository;

import java.math.BigDecimal;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ClientService clientService;

    public PaymentRepository getPaymentRepository() {
        return paymentRepository;
    }

    @Transactional(readOnly = true)
    public Page<Payment> listAll(Pageable pageable) {
        return getPaymentRepository().findAll(pageable);
    }

    public Payment findByIdOrThrowBackBadRequestException(Long id) {
        return getPaymentRepository().findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "Payment's information not found"));
    }

    @Transactional
    public void delete(Long id){
        getPaymentRepository().delete(findByIdOrThrowBackBadRequestException(id));
    }

    @Transactional
    public void replace(PaymentPutRequestBody paymentPutRequestBody) {
        Payment payment = findByIdOrThrowBackBadRequestException(paymentPutRequestBody.getPaymentId());
        payment.setPaymentId(payment.getPaymentId());
        payment.setClientCpf(payment.getClientCpf());
        payment.setPointsSale(paymentPutRequestBody.getPointsSale());
        getPaymentRepository().save(payment);
    }
    private Client clientCpf;
    private BigDecimal pointsSale;
    private String paymentDate;
    private Boolean status; // 0 - started; 1 - finished;

    @Transactional
    public Payment save(@Validated PaymentPostRequestBody paymentPostRequestBody) {
        clientService.findByCpfOrThrowBackBadRequestException(paymentPostRequestBody.getClientCpf().getClientCpf());
        return getPaymentRepository().save(Payment.builder().clientCpf(paymentPostRequestBody.getClientCpf())
                .pointsSale(paymentPostRequestBody.getPointsSale()).build());
    }

    public Page<Payment> findByClientCpfOrThrowBackBadRequestException(String clientCpf, Pageable pageable) {
        existsByClientCpf(clientCpf);
        return getPaymentRepository().findByClientCpf(clientCpf,pageable);
    }

    public void existsByClientCpf(String clientCpf){
        if (!getPaymentRepository().existsByClientCpf(clientCpf)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payments of Client with CPF "
                      +clientCpf+" not found");
        }
    }
}
