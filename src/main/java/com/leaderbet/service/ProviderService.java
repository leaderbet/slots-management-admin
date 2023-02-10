package com.leaderbet.service;

import com.leaderbet.Entity.Provider;
import com.leaderbet.repository.ProviderRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProviderService {
    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public List<Provider> search(Boolean includeDeleted, String platformName, String providerName, Integer providerId) {
        return providerRepository.findAll((root, query, cb) -> {

            Predicate predicate = cb.conjunction();
            if (!includeDeleted) {
                predicate = cb.and(predicate, cb.isNull(root.get("deletedAt")));
            }
            if (StringUtils.hasText(platformName)) {
                predicate = cb.and(predicate, cb.equal(root.get("platformName"), platformName));
            }
            if (StringUtils.hasText(providerName)) {
                predicate = cb.and(predicate, cb.equal(root.get("providerName"), providerName));
            }
            if (providerId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("providerId"), providerId));
            }
            return predicate;
        });
    }


    public Provider add(Provider provider) {
        return providerRepository.save(provider);
    }

    public void delete(Integer id) {
        var provider = providerRepository.findById(id);
        provider.ifPresent(p -> {
            p.setDeletedAt(LocalDateTime.now());
            providerRepository.save(p);
        });
    }

    public Provider edit(Provider provider) {
        return providerRepository.findById(provider.getId())
                .map(existingProvider -> {
                    var providerToUpdate = new Provider(
                            existingProvider.getId(),
                            provider.getPlatformName(),
                            provider.getProviderName(),
                            provider.getOperatorId(),
                            provider.getEnabled());
                    return providerRepository.save(providerToUpdate);
                })
//                .orElseGet(() -> add(provider));
                .orElseThrow();
    }

    public void changeProviderStatus(int id, int status) {
        providerRepository.findById(id)
                .map(existingProvider -> {
                    existingProvider.setEnabled(status);
                    return providerRepository.save(existingProvider);
                })
                .orElseThrow();
    }
}
