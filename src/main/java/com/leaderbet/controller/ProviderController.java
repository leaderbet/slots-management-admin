package com.leaderbet.controller;

import com.leaderbet.Entity.Provider;
import com.leaderbet.service.ProviderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/slots_management/providers")
public class ProviderController {
    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @GetMapping
    public List<Provider> search(@RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
                                 @RequestParam(required = false) String platformName,
                                 @RequestParam(required = false) String providerName,
                                 @RequestParam(required = false) Integer providerId) {
        return providerService.search(includeDeleted, platformName, providerName, providerId);
    }

    @PostMapping("/add")
    public Provider add(@RequestBody Provider provider) {
        return providerService.add(provider);
    }

    @PutMapping("/{id}/edit")
    public Provider edit(@PathVariable int id,
                         @RequestBody Provider provider) {
        provider.setId(id);
        return providerService.edit(provider);
    }

    @DeleteMapping("/{id}/delete")
    public void delete(@PathVariable int id) {
        providerService.delete(id);
    }

    @PutMapping("/{id}/change_status")
    public void changeProviderStatus(@PathVariable int id,
                                     @RequestParam int status) {
        providerService.changeProviderStatus(id, status);
    }
}
