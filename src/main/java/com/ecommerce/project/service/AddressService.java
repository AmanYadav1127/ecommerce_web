package com.ecommerce.project.service;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAddresses();

    List<AddressDTO> getAddressesByAddressId(Long addressId);

    List<AddressDTO> getAddressesByUser(User user);

    AddressDTO updateAddressById(Long addressId,AddressDTO addressDTO);

    String deleteAddressById(Long addressId);
}
