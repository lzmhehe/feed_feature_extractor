package com.dishan.ffe.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EsService {
    @Autowired
    EsRepository esRepository;

    public List<Hotel> getHotelFromTitle(String keyword) {
        return esRepository.findByTitleLike(keyword);//调用搜索方法
    }

    public Hotel addHotel(Hotel hotel) {
        try {
            return esRepository.save(hotel);
        } catch (RuntimeException e) {
            // es client 版本和 server不匹配造成的，暂时ignore
            return hotel;
        }
    }
}