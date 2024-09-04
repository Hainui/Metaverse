package com.metaverse.region.Controller;

import com.metaverse.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 区服表 前端控制器
 * </p>
 *
 * @author Hainui
 * @since 2024-09-03
 */
@RestController
@RequestMapping("/region")
@RequiredArgsConstructor
public class RegionController {
    private final RegionService regionService;

    @GetMapping("/getAllRegionNames")
    public ResponseEntity<List<String>> getAllRegionNames() {
        List<String> regionNames = regionService.getAllRegionNames();
        return ResponseEntity.ok(regionNames);
    }
}
