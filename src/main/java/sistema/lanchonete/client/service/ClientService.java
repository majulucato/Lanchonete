package sistema.lanchonete.client.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import sistema.lanchonete.client.domain.Client;
import sistema.lanchonete.client.dto.ClientPoints;
import sistema.lanchonete.client.dto.ClientPostRequestBody;
import sistema.lanchonete.client.dto.ClientPutRequestBody;
import sistema.lanchonete.client.repository.ClientRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientRepository getClientRepository() {
        return clientRepository;
    }
    @Transactional(readOnly = true)

    public Page<Client> listAll(Pageable pageable) {
        return getClientRepository().findAll(pageable);
    }

    public Client findByIdOrThrowBackBadRequestException(Long id) {
        return getClientRepository().findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client not found"));
    }
    public Client findByCpfOrThrowBackBadRequestException(String clientCpf) {
        return getClientRepository().findByClientCpf(clientCpf);
    }
    public void existsByCpf(String clientCpf) {
        if (getClientRepository().existsClientCpf(clientCpf)==true){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client with CPF "+clientCpf+" already exists");
        }
    }

    @Transactional
    public void delete(Long id){
        getClientRepository().delete(findByIdOrThrowBackBadRequestException(id));
    }

    @Transactional
    public void replace(ClientPutRequestBody clientPutRequestBody) {
        Client client = findByIdOrThrowBackBadRequestException(clientPutRequestBody.getClientId());
        client.setClientId(client.getClientId());
        client.setClientName(clientPutRequestBody.getClientName());
        client.setClientCpf(clientPutRequestBody.getClientCpf());
        client.setClientCnpj(clientPutRequestBody.getClientCnpj());
        client.setClientPoints(clientPutRequestBody.getClientPoints());
        client.setClientPhone(clientPutRequestBody.getClientPhone());
        getClientRepository().save(client);
    }

    @Transactional
    public Client save(@Validated ClientPostRequestBody clientPostRequestBody) {
        existsByCpf(clientPostRequestBody.getClientCpf());
        return getClientRepository().save(Client.builder().clientName(clientPostRequestBody.getClientName())
                .clientCpf(clientPostRequestBody.getClientCpf()).clientCnpj(clientPostRequestBody.getClientCnpj())
                .clientPhone(clientPostRequestBody.getClientPhone())
                .clientPoints(BigDecimal.valueOf(0)).build());
    }

    public void addPoints(ClientPoints clientPoints) {
        Client client = findByIdOrThrowBackBadRequestException(clientPoints.getClientId());
        client.setClientPoints(clientPoints.getClientPoints());
        getClientRepository().save(client);
    }
}