using Microsoft.EntityFrameworkCore;
using QuickFeedback.Api.Data;
using System.Collections;

var builder = WebApplication.CreateBuilder(args);


// Add services to the container
builder.Services.AddControllers();

// Configure Database
var connectionString = Environment.GetEnvironmentVariable("SQLAZURECONNSTR_quickfeedbackdb");

builder.Services.AddDbContext<FeedbackDbContext>(options =>
    options.UseSqlServer(connectionString));

// Configure CORS
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowFrontend", policy =>
    {
        policy.WithOrigins(
                builder.Configuration["AllowedOrigins"] ?? "http://localhost:3000",
                "http://localhost:3000",
                "https://*.azurestaticapps.net",
                "https://quickfeedbackfrontend.z1.web.core.windows.net"
            )
            .AllowAnyMethod()
            .AllowAnyHeader();
    });
});

var app = builder.Build();

app.UseHttpsRedirection();
app.UseCors("AllowFrontend");
app.UseAuthorization();
app.MapControllers();

// Auto-migrate database on startup
using (var scope = app.Services.CreateScope())
{
    var dbContext = scope.ServiceProvider.GetRequiredService<FeedbackDbContext>();
    dbContext.Database.Migrate();
}

app.Run();
