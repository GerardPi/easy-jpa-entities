package com.github.gerardpi.easy.jpaentities;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UuidEncoderTest extends SimpleScenarioTest<UuidEncoderTest.State> {
    private static final Logger LOG = LoggerFactory.getLogger(UuidEncoderTest.class);
    private static final String UUID_1 = "00000001-1111-2222-3333-444444444444";
    private static final String UUID_2 = "b63f85c6-8331-46d3-b8b7-f64590f99f04";
    private static final String ENC_UUID_1 = "AAAAARERIiIzM0RERERERA";
    private static final String ENC_UUID_2_TOO_SHORT = "tj-FxoMxRtO4t_ZFkPmfB";
    private static final String ENC_UUID_2 = ENC_UUID_2_TOO_SHORT + "A";


    @Test
    public void happy_flow_convert_to_base64_and_back_1() {
        when().encoding_a_UUID_represented_by_string_$(UUID_1);
        then().that_ID_as_a_base64_encoded_string_is_$(ENC_UUID_1);
        when().decoding_a_base64_encoded_string_$_to_UUID(ENC_UUID_1);
        when().the_resulting_UUID_is_$(UUID_1);
    }
    @Test
    public void happy_flow_convert_to_base64_and_back_2() {
        when().encoding_a_UUID_represented_by_string_$(UUID_2);
        then().that_ID_as_a_base64_encoded_string_is_$(ENC_UUID_2);
        when().decoding_a_base64_encoded_string_$_to_UUID(ENC_UUID_2);
        when().the_resulting_UUID_is_$(UUID_2);
    }

    @Test
    public void incomplete_base64_string() {
        when().decoding_a_base64_encoded_string_$_to_UUID("tj-FxoMxRtO4t_ZFkPmfB");
        when().and_error_occurs_and_the_error_message_starts_with_$(UuidEncoder.ERROR_MESSAGE_INVALID_LENGTH);
    }

    static class State extends Stage<State> {
        private final UuidEncoder sut;
        private String uuidAsBase64EncodedString;
        private UUID uuid;
        private Throwable throwable;

        State() {
            sut = new UuidEncoder();
        }

        State encoding_a_UUID_represented_by_string_$(@Quoted String givenUuid) {
            this.uuidAsBase64EncodedString = sut.encode(UUID.fromString(givenUuid));
            return self();
        }

        State that_ID_as_a_base64_encoded_string_is_$(@Quoted String expectedUuidAsString) {
            assertThat(this.uuidAsBase64EncodedString).isEqualTo(expectedUuidAsString);
            return self();
        }

        State decoding_a_base64_encoded_string_$_to_UUID(@Quoted String givenBase64EncodedUuid) {
            try {
                uuid = sut.decode(givenBase64EncodedUuid);
            } catch (IllegalArgumentException e) {
                LOG.info("Caught a {}: '{}", e.getClass().getSimpleName(), e.getMessage());
                this.throwable = e;
            }
            return self();
        }

        State the_resulting_UUID_is_$(@Quoted String expectedUuid) {
            assertThat(uuid.toString()).isEqualTo(expectedUuid);
            return self();
        }

        State and_error_occurs_and_the_error_message_starts_with_$(String expectedMessagePrefix) {
            assertThat(this.throwable).isNotNull();
            assertThat(this.throwable.getMessage()).startsWith(expectedMessagePrefix);
            return self();
        }
    }
}
