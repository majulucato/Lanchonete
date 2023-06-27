package sistema.lanchonete.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sistema.lanchonete.payment.domain.Payment;
import sistema.lanchonete.payment.dto.PaymentPostRequestBody;
import sistema.lanchonete.payment.dto.PaymentPutRequestBody;
import sistema.lanchonete.payment.service.PaymentService;

@RestController
@RequestMapping("payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentService getPaymentService() {
        return paymentService;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Page<Payment>> listAll(@PageableDefault(page=0, size=10,
            sort = "paymentId", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.ok(getPaymentService().listAll(pageable));
    }
    @GetMapping(path = "/{id}")
    public ResponseEntity<Payment> findById(@PathVariable long id){
        return ResponseEntity.ok(getPaymentService().findByIdOrThrowBackBadRequestException(id));
    }
    @GetMapping(path = "/{clientId}")
    public ResponseEntity<Page<Payment>> findByClientId(@PathVariable String clientCpf, @PageableDefault(page=0,
            size=10, sort = "paymentId", direction = Sort.Direction.ASC)Pageable pageable){
        return ResponseEntity.ok(getPaymentService().findByClientCpfOrThrowBackBadRequestException(clientCpf,pageable));
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        getPaymentService().delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PostMapping
    public ResponseEntity<Payment> save(@RequestBody PaymentPostRequestBody paymentPostRequestBody){
        return ResponseEntity.ok(getPaymentService().save(paymentPostRequestBody));
    }
    @PutMapping(path = "/{id}")
    public ResponseEntity<Payment> replace(@PathVariable long id,
                                           @RequestBody PaymentPutRequestBody paymentPutRequestBody){
        getPaymentService().replace(paymentPutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
