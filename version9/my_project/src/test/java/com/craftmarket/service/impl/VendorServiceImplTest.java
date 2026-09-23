package com.craftmarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.craftmarket.entity.Vendor;
import com.craftmarket.mapper.VendorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorServiceImplTest {

    @Mock
    private VendorMapper vendorMapper;

    @InjectMocks
    private VendorServiceImpl vendorService;

    private Vendor testVendor;

    @BeforeEach
    void setUp() {
        testVendor = new Vendor();
        testVendor.setId(1L);
        testVendor.setName("测试摊主");
        testVendor.setPhone("13800138000");
        testVendor.setBusinessType("手工艺品");
        testVendor.setAddress("测试地址");
    }

    @Test
    void saveVendor_Success() {
        // Arrange
        when(vendorMapper.insert(any(Vendor.class))).thenReturn(1);
        when(vendorMapper.selectByPhone(anyString())).thenReturn(null);

        // Act
        boolean result = vendorService.saveVendor(testVendor);

        // Assert
        assertTrue(result);
        verify(vendorMapper).insert(testVendor);
    }

    @Test
    void saveVendor_PhoneExists() {
        // Arrange
        Vendor existingVendor = new Vendor();
        existingVendor.setId(2L);
        existingVendor.setPhone("13800138000");
        when(vendorMapper.selectByPhone(anyString())).thenReturn(existingVendor);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            vendorService.saveVendor(testVendor);
        });
        
        assertEquals("该手机号已被使用", exception.getMessage());
        verify(vendorMapper, never()).insert(any(Vendor.class));
    }

    @Test
    void updateVendor_Success() {
        // Arrange
        when(vendorMapper.updateById(any(Vendor.class))).thenReturn(1);
        when(vendorMapper.selectByPhone(anyString())).thenReturn(null);

        // Act
        boolean result = vendorService.updateVendor(testVendor);

        // Assert
        assertTrue(result);
        verify(vendorMapper).updateById(testVendor);
    }

    @Test
    void updateVendor_PhoneExistsByOtherUser() {
        // Arrange
        Vendor existingVendor = new Vendor();
        existingVendor.setId(2L);
        existingVendor.setPhone("13800138000");
        when(vendorMapper.selectByPhone(anyString())).thenReturn(existingVendor);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            vendorService.updateVendor(testVendor);
        });
        
        assertEquals("该手机号已被使用", exception.getMessage());
        verify(vendorMapper, never()).updateById(any(Vendor.class));
    }

    @Test
    void getVendorById_Success() {
        // Arrange
        when(vendorMapper.selectById(anyLong())).thenReturn(testVendor);

        // Act
        Vendor result = vendorService.getVendorById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试摊主", result.getName());
        verify(vendorMapper).selectById(1L);
    }

    @Test
    void getVendorByPhone_Success() {
        // Arrange
        when(vendorMapper.selectByPhone(anyString())).thenReturn(testVendor);

        // Act
        Vendor result = vendorService.getVendorByPhone("13800138000");

        // Assert
        assertNotNull(result);
        assertEquals("测试摊主", result.getName());
        verify(vendorMapper).selectByPhone("13800138000");
    }

    @Test
    void getVendorsPage_Success() {
        // Arrange
        Page<Vendor> page = new Page<>(1, 10);
        List<Vendor> vendors = Arrays.asList(testVendor);
        page.setRecords(vendors);
        
        when(vendorMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
            .thenReturn(page);

        // Act
        Page<Vendor> result = vendorService.getVendorsPage(1, 10, "手工艺品");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals("测试摊主", result.getRecords().get(0).getName());
        verify(vendorMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void getVendorsByBusinessType_Success() {
        // Arrange
        List<Vendor> vendors = Arrays.asList(testVendor);
        when(vendorMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(vendors);

        // Act
        List<Vendor> result = vendorService.getVendorsByBusinessType("手工艺品");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试摊主", result.get(0).getName());
        verify(vendorMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void deleteVendor_Success() {
        // Arrange
        when(vendorMapper.deleteById(anyLong())).thenReturn(1);

        // Act
        boolean result = vendorService.deleteVendor(1L);

        // Assert
        assertTrue(result);
        verify(vendorMapper).deleteById(1L);
    }
}