package org.pfemanager.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pfemanager.model.ResetToken;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResetTokenDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<ResetToken> typedQuery;

    @Mock
    private Query query;

    @InjectMocks
    private ResetTokenDAO resetTokenDAO;

    @Test
    void testSave() {
        // Given
        ResetToken token = new ResetToken();
        token.setToken("uuid-123");

        // When
        resetTokenDAO.save(token);

        // Then
        verify(em).persist(token);
    }

    @Test
    void testFindByToken_Found() {
        // Given
        String tokenStr = "valid-token";
        ResetToken expected = new ResetToken();
        expected.setToken(tokenStr);

        when(em.createQuery(anyString(), eq(ResetToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("token", tokenStr)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(expected));

        // When
        Optional<ResetToken> result = resetTokenDAO.findByToken(tokenStr);

        // Then
        assertTrue(result.isPresent());
        assertEquals(tokenStr, result.get().getToken());
    }

    @Test
    void testUpdate() {
        // Given
        ResetToken token = new ResetToken();
        token.setToken("update-me");

        // When
        resetTokenDAO.update(token);

        // Then
        verify(em).merge(token);
    }

    @Test
    void testDeleteByEmail() {
        // Given
        String email = "test@etu.ma";
        // On simule la chaîne DELETE : createQuery -> setParameter -> executeUpdate
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter("email", email)).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1); // Simule 1 ligne supprimée

        // When
        resetTokenDAO.deleteByEmail(email);

        // Then
        verify(em).createQuery(contains("DELETE FROM ResetToken"));
        verify(query).setParameter("email", email);
        verify(query).executeUpdate();
    }
}