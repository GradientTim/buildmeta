package dev.gradienttim.buildmeta.sample;

import dev.gradienttim.buildmeta.generated.BuildMeta;
import dev.gradienttim.buildmeta.generated.GitMeta;
import dev.gradienttim.buildmeta.generated.Types;
import dev.gradienttim.buildmeta.generated.adapters.Adapters;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class Main {
    static void main() throws IOException {
        System.out.printf("%s v%s built by %s%n", BuildMeta.appName, BuildMeta.APP_VERSION, BuildMeta.buildUser);
        System.out.printf("Git commit hash = %s%n", GitMeta.COMMIT_HASH);

        System.out.printf("greeting = %s", Types.GREETING);
        System.out.printf("letter = %s, answer = %d, bigNumber = %d%n", Types.LETTER, Types.ANSWER, Types.BIG_NUMBER);
        System.out.printf("pi = %s, ratio = %s, enabled = %s%n", Types.PI, Types.RATIO, Types.ENABLED);
        System.out.printf("id = %s, timeout = %s, releasedAt = %s%n", Types.ID, Types.TIMEOUT, Types.RELEASED_AT);
        System.out.printf("releaseDate = %s, releaseTime = %s%n", Types.RELEASE_DATE, Types.RELEASE_TIME);
        System.out.printf("releaseDateTime = %s%n", Types.RELEASE_DATE_TIME);
        System.out.printf("releaseOffsetDateTime = %s%n", Types.RELEASE_OFFSET_DATE_TIME);
        System.out.printf("releaseZonedDateTime = %s%n", Types.RELEASE_ZONED_DATE_TIME);
        System.out.printf("rawName = %s%n", Types.rawName);

        System.out.printf("usernamePattern matches 'tim' = %s%n", Adapters.usernamePattern.matcher("tim").matches());
        System.out.printf("website = %s%n", Adapters.website);

        try (InputStream input = Main.class.getResourceAsStream("/app.properties")) {
            assert input != null;
            System.out.printf("%napp.properties:%n%s", new String(input.readAllBytes(), StandardCharsets.UTF_8));
        }
    }
}
