package com.sh.post.controller;

import com.sh.post.bean.User;
import com.sh.post.dao.UserDaoService;
import com.sh.post.exception.UserNotFoundException;
import com.sh.post.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/jpa")
@RequiredArgsConstructor
public class UserJPAController {
    private final UserRepository userRepository;

    // /jpa/users
    @GetMapping("/users")
    public List<User> retrieveAllUsers() {
        return userRepository.findAll();
        /*
            리스트 형태로 반환하면 유연성 떨어짐. 만약 전체사용자수와 같이 출력해야 한다고하면 수정 어려움
            응답 데이터에 맞는 객체 만들고 그 일부로서 유저 리스트 넣을것.
            여기선 Service 계층이 없지만 총 사용자수를 넣는다면 유저리스트와 사용자수 쿼리를 트랜잭션(query니까 read-only로 하면 될 듯) 안에서 처리 한 후 DTO에 넣어서 반환하도록 해서
            Controller가 entity에 대해 모르도록 하는게 나을지도? 
         */
    }

    // /jpa/users/{id}
    @GetMapping("/users/{id}")
    public ResponseEntity retrieveUserById(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);

        if(user.isEmpty()) {
            throw new UserNotFoundException("id -" + id);
        }
        EntityModel<User> userEntityModel = EntityModel.of(user.get());

        WebMvcLinkBuilder linkBuilder = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        userEntityModel.add(linkBuilder.withRel("all-users")); // all-users -> http://localhost:8088/users
        return ResponseEntity.ok(userEntityModel);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUserById(@PathVariable int id) {
        userRepository.deleteById(id);
    }

    // jpa/users
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userRepository.save(user);

        // 현재 요청 기반 URI 구성 
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        // WebMvcLinkBuilder를 이용한 URI 구성
        URI uri = linkTo(methodOn(this.getClass()).retrieveUserById(savedUser.getId())).toUri();
        return ResponseEntity.created(uri).build();
    }
}
