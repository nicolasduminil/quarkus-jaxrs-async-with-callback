package fr.simplex_software.quarkus_simple.tests;

import io.quarkus.test.junit.*;
import jakarta.ws.rs.client.*;
import org.eclipse.microprofile.config.inject.*;
import org.junit.jupiter.api.*;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
public class TimeResourceIT
{
  private static final String FMT = "d MMM uuuu, HH:mm:ss";

  @ConfigProperty(name = "timeSrvUrl")
  String timeSrvUrl;

  @Test
  public void testWithPolling()
  {
    try (Client client = ClientBuilder.newClient())
    {
      Future<String> timeFuture = client.target(timeSrvUrl).request().async().get(String.class);
      String time = timeFuture.get(5, TimeUnit.SECONDS);
      assertThat(LocalDateTime.parse(time, DateTimeFormatter.ofPattern(FMT)))
        .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.MINUTES));
    }
    catch (Exception ex)
    {
      fail("### TimeResourceIT.testWithPolling(): Unexpected exception %s", ex.getMessage());
    }
  }

  @Test
  public void testWithCallback()
  {
    try (Client client = ClientBuilder.newClient())
    {
      client.target(timeSrvUrl).request().async().get(new TimeCallback());
    }
    catch (Exception ex)
    {
      fail("### TimeResourceIT.testWithCallback): Unexpected exception %s", ex.getMessage());
    }
  }

  private static class TimeCallback implements InvocationCallback<String>
  {
    @Override
    public void completed(String time)
    {
      System.out.println ("### TimeCallback.completed: enter");
      assertThat(LocalDateTime.parse(time, DateTimeFormatter.ofPattern(FMT)))
        .isCloseTo(LocalDateTime.now(), byLessThan(1, ChronoUnit.MINUTES));
      assertThat(false).isTrue();
      System.out.println ("### TimeCallback.completed: exit");
    }

    @Override
    public void failed(Throwable throwable)
    {
      fail("### CurrentTimeResourceIT.TimeCallback.failed(): Unexpected exception %s", throwable.getMessage());
    }
  }
}
