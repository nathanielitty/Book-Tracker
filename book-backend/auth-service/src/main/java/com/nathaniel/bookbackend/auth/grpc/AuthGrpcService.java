package com.nathaniel.bookbackend.auth.grpc;

import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Claims;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import com.nathaniel.bookbackend.auth.service.JwtService;
import com.nathaniel.bookbackend.grpc.*;

import java.util.List;

@GrpcService
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {
    
    @Autowired
    private JwtService jwtService;

    @Override
    public void validateToken(TokenValidationRequest request, StreamObserver<TokenValidationResponse> responseObserver) {
        String token = request.getToken();
        Claims claims = jwtService.validateToken(token);
        
        TokenValidationResponse response;
        if (claims != null) {
            response = TokenValidationResponse.newBuilder()
                    .setValid(true)
                    .setUserId(claims.getSubject())
                    .addAllRoles(claims.get("roles", List.class))
                    .build();
        } else {
            response = TokenValidationResponse.newBuilder()
                    .setValid(false)
                    .build();
        }
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        String userId = request.getUserId();
        // Implement user details retrieval logic here
        UserResponse response = UserResponse.newBuilder()
                .setId(userId)
                // Add more user details as needed
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
