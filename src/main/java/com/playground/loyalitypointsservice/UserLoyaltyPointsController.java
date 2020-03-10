package com.playground.loyalitypointsservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
public class UserLoyaltyPointsController {
    private static final String USER_API_BASE = "/users";

    private UserLoyaltyService userLoyaltyService;

    @Autowired
    public UserLoyaltyPointsController(UserLoyaltyService userLoyaltyService) {
        this.userLoyaltyService = userLoyaltyService;
    }

    @GetMapping(path = USER_API_BASE)
    public ResponseEntity<List<UserLoyaltyDto>> getUser() {
        Set<UUID> usersIds = userLoyaltyService.getUsersIds();

        List<UserLoyaltyDto> users = new ArrayList<>();
        for (UUID id : usersIds) {
            UserLoyaltyDto userDto = createUserDtoFromUUID(id);
            users.add(userDto);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @GetMapping(USER_API_BASE + "/{id}")
    public ResponseEntity<UserLoyaltyDto> getUsers(@PathVariable String id) {
        UUID uuid = UUID.fromString(id);

        long availPoints = userLoyaltyService.getUserAvailablePoints(uuid);
        long pendingPoints = userLoyaltyService.getUserPendingPoints(uuid);

        UserLoyaltyDto userLoyaltyDto = new UserLoyaltyDto();
        userLoyaltyDto.setPendingPoints(pendingPoints);
        userLoyaltyDto.setPoints(availPoints);
        userLoyaltyDto.setHref(makeLink(USER_API_BASE + "/%s", id));
        userLoyaltyDto.setUuid(id);

        return new ResponseEntity<>(userLoyaltyDto, HttpStatus.OK);
    }

    private UserLoyaltyDto createUserDtoFromUUID(UUID id) {
        UserLoyaltyDto userLoyaltyDto = new UserLoyaltyDto();
        userLoyaltyDto.setUuid(id.toString());
        userLoyaltyDto.setHref(makeLink(USER_API_BASE + "/%s", id.toString()));
        return userLoyaltyDto;
    }

    public static String makeLink(String urlPath, Object... arguments) {
        final HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        final String baseUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ':' + httpServletRequest.getServerPort();

        return baseUrl + String.format(urlPath, encodeArguments(arguments));
    }

    private static Object[] encodeArguments(Object... params) {
        if (params.length == 0) {
            return new String[0];
        }

        final Object[] encoded = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            encoded[i] = urlEncode(Objects.toString(params[i]));
        }
        return encoded;
    }

    private static String urlEncode(String value) {
        return UriUtils.encodePath(value, StandardCharsets.UTF_8.name()).replace("/", "%2F");
    }
}
