package com.leaderbet.service;

import com.leaderbet.Entity.Provider;
import com.leaderbet.repository.ProviderRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    private Provider getById(int id) {
        Optional<Provider> p = providerRepository.findById(id);
        if (p.isEmpty()) throw new NoSuchElementException();

        return p.get();
    }

    public Provider add(Provider provider) {
        return providerRepository.save(provider);
    }

    @Transactional
    public void delete(Integer id) {
        var provider = getById(id);
        provider.setDeletedAt(LocalDateTime.now());
    }

    public Provider edit(Provider provider) {
        return providerRepository.save(provider);
    }

    @Transactional
    public void changeProviderStatus(int id, int status) {
        var provider = getById(id);
        provider.setEnabled(status);
    }

}
