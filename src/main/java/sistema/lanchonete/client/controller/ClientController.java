package sistema.lanchonete.client.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sistema.lanchonete.client.domain.Client;
import sistema.lanchonete.client.dto.ClientPoints;
import sistema.lanchonete.client.dto.ClientPostRequestBody;
import sistema.lanchonete.client.dto.ClientPutRequestBody;
import sistema.lanchonete.client.service.ClientService;

@RestController
@RequestMapping("client")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    public ClientService getClientService() {
        return clientService;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Page<Client>> listAll(@PageableDefault(page=0, size=10,
            sort = "clientId", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.ok(getClientService().listAll(pageable));
    }
   @GetMapping(path = "/{id}")
    public ResponseEntity<Client> findById(@PathVariable long id){
        return ResponseEntity.ok(getClientService().findByIdOrThrowBackBadRequestException(id));
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        getClientService().delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PostMapping
    public ResponseEntity<Client> save(@RequestBody ClientPostRequestBody clientPostRequestBody){
        return ResponseEntity.ok(getClientService().save(clientPostRequestBody));
    }
    @PutMapping(path = "/{id}")
    public ResponseEntity<Client> replace(@PathVariable long id,
                                          @RequestBody ClientPutRequestBody clientPutRequestBody){
        getClientService().replace(clientPutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping(path = "/{id}/add-points")
    public ResponseEntity<Client> replace(@PathVariable long id,
                                          @RequestBody ClientPoints clientPoints){
        getClientService().addPoints(clientPoints);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
