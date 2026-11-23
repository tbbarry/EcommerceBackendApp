package com.backend.ecommerce.controller;

import com.backend.ecommerce.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ImageControllerTest {

    @TempDir
    Path uploadDir;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ImageController controller = new ImageController(mock(ImageService.class));
        ReflectionTestUtils.setField(controller, "uploadDir", uploadDir.toString());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void servesAnImageInAProductDirectory() throws Exception {
        Path directory = Files.createDirectory(uploadDir.resolve("product_1001"));
        byte[] image = {1, 2, 3};
        Files.write(directory.resolve("image.png"), image);

        mockMvc.perform(get("/api/images/products/product_1001/image.png"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(image));
    }

    @Test
    void keepsServingImagesAtTheRootOfTheUploadDirectory() throws Exception {
        byte[] image = {4, 5, 6};
        Files.write(uploadDir.resolve("image.jpg"), image);

        mockMvc.perform(get("/api/images/products/image.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(image));
    }

    @Test
    void returnsNotFoundForMissingImages() throws Exception {
        mockMvc.perform(get("/api/images/products/product_1001/missing.png"))
                .andExpect(status().isNotFound());
    }

    @Test
    void refusesPathsOutsideTheUploadDirectory() throws Exception {
        Files.write(uploadDir.resolve("outside.png"), new byte[]{1});
        Path root = Files.createDirectory(uploadDir.resolve("products"));
        ImageController controller = new ImageController(mock(ImageService.class));
        ReflectionTestUtils.setField(controller, "uploadDir", root.toString());

        assertEquals(404, controller.getProductImage("../outside.png").getStatusCode().value());
    }
}
