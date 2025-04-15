package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Client;
import com.example.usermanagementbackend.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client saveClient(Client client) {
        Optional<Client> existing = clientRepository.findByEmail(client.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Un client avec cet email existe déjà.");
        }
        if (client.getCreditLimit() == null) {
            client.setCreditLimit(1000.0);
        }
        if (client.getPreferences() == null) {
            client.setPreferences("CREDIT_CARD");
        }
        return clientRepository.save(client);
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public Optional<Client> getClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client updateClient(Long id, Client client) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID: " + id));
        existing.setName(client.getName());
        existing.setEmail(client.getEmail());
        existing.setPhone(client.getPhone());
        existing.setAddress(client.getAddress());
        existing.setCreditLimit(client.getCreditLimit());
        existing.setPreferences(client.getPreferences());
        return clientRepository.save(existing);
    }

    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client non trouvé avec l'ID: " + id);
        }
        clientRepository.deleteById(id);
    }
}