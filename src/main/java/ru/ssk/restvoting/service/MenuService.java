package ru.ssk.restvoting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.ssk.restvoting.model.MenuItem;
import ru.ssk.restvoting.repository.MenuDataJpaRepository;
import ru.ssk.restvoting.to.MenuItemDisplay;
import ru.ssk.restvoting.to.MenuItemTo;
import ru.ssk.restvoting.util.MenuUtil;
import ru.ssk.restvoting.util.ValidationUtil;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class MenuService {
    private final MenuDataJpaRepository crudRepository;
    private final RestaurantService restaurantService;
    private final DishService dishService;

    @Autowired
    public MenuService(MenuDataJpaRepository crudRepository, RestaurantService restaurantService, DishService dishService) {
        this.crudRepository = crudRepository;
        this.restaurantService = restaurantService;
        this.dishService = dishService;
    }

    @Cacheable("votingMenu")
    public List<MenuItemDisplay> getAllForVoting(int restaurantId, Date date) {
        return crudRepository.getAllForDisplay(restaurantId, date);
    }

    public List<MenuItemDisplay> getAll(int restaurantId, Date date) {
        if (date == null) date = Date.valueOf(LocalDate.now());
        return crudRepository.getAllForDisplay(restaurantId, date);
    }

    public MenuItem get(int id) {
        return crudRepository.findById(id).orElse(null);
    }

    public MenuItemTo getTo(int id) {
        return ValidationUtil.checkNotFoundWithId(MenuUtil.asTo(crudRepository.findById(id).orElse(null)), id);
    }

    @CacheEvict(value = "votingMenu", allEntries = true)
    public void update(MenuItemTo menuItemTo) {
        Assert.notNull(menuItemTo, "Menu item must not be null");
        Assert.notNull(menuItemTo.getId(), "Menu item id must not be null");
        MenuItem menuItem = createFromMenuItemTo(menuItemTo);
        crudRepository.save(menuItem);
    }

    @CacheEvict(value = "votingMenu", allEntries = true)
    public MenuItem create(MenuItemTo menuItemTo) {
        Assert.notNull(menuItemTo, "Menu item must not be null");
        MenuItem menuItem = createFromMenuItemTo(menuItemTo);
        return crudRepository.save(menuItem);
    }

    private MenuItem createFromMenuItemTo(MenuItemTo menuItemTo) {
        Objects.requireNonNull(menuItemTo);
        return new MenuItem(menuItemTo.getId(),
                restaurantService.getReference(menuItemTo.getRestaurantId()),
                menuItemTo.getDate(),
                dishService.getReference(menuItemTo.getDishId()),
                (int) (menuItemTo.getPrice() * 100));
    }

    @CacheEvict(value = "votingMenu", allEntries = true)
    public void delete(int id) {
        ValidationUtil.checkNotFoundWithId(crudRepository.delete(id) != 0, id);
    }
}
