package fr.simplex_software.quarkus_simple;

import jakarta.enterprise.context.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.time.*;
import java.time.format.*;

@ApplicationScoped
@Path("time")
@Produces(MediaType.TEXT_PLAIN)
public class TimeResource
{
  private static final String FMT = "d MMM uuuu, HH:mm:ss";

  @GET
  public String currentTime()
  {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern(FMT));
  }
}
