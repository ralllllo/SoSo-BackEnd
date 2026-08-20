package com.soso.domain.account.services;

import com.soso.domain.account.dao.AccountDAO;
import com.soso.domain.account.dto.AccountRelationRequestDto;
import com.soso.domain.account.dto.AccountRelationResponseDto;
import com.soso.domain.account.dto.AccountSearchResponseDto;
import com.soso.domain.account.dto.ItemResponseDto;
import com.soso.domain.account.dto.PartnerDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AccountService {

    @Autowired
    private AccountDAO accountDAO;

    
    public List<AccountSearchResponseDto> searchAccounts(String searchTerm) {
        return accountDAO.searchPartnerStores(searchTerm);
    }

    
    public List<AccountSearchResponseDto> getAllPartnerStores(String searchTerm, String city, String district) {

        return accountDAO.getAllPartnerStores(searchTerm, city, district);
    }

    
    @Transactional
    public boolean registerAccount(AccountRelationRequestDto relationDto) {
        return accountDAO.insertPartnerRelation(relationDto) > 0;
    }

    
    public List<AccountRelationResponseDto> getRegisteredAccounts(int businessSeq, String searchTerm, String city, String district) {

        return accountDAO.getPartnerRelationsByBusinessSeq(businessSeq, searchTerm, city, district);

    }

    
    public List<ItemResponseDto> getPartnerItems(int partnerSeq) {
        return accountDAO.getItemsByPartnerSeq(partnerSeq);
    }

    
    @Transactional
    public boolean deleteAccount(int relationSeq) {
        return accountDAO.deletePartnerRelation(relationSeq) > 0;
    }

    
    public Integer getFirstStoreSeqByUserSeq(int userSeq) {
        return accountDAO.getFirstStoreSeqByUserSeq(userSeq);
    }

    
 
    public List<AccountSearchResponseDto> myPartners(Long storeSeq) {
        return accountDAO.myPartners(storeSeq);
    }

    
    public AccountSearchResponseDto getPartnerDetail(int partnerSeq) {
        return accountDAO.getPartnerDetail(partnerSeq);

    }
}
