using Microsoft.EntityFrameworkCore;
using QuickFeedback.Api.Data;
using System.Collections;

var builder = WebApplication.CreateBuilder(args);


// Add services to the container
builder.Services.AddControllers();

// Configure Database
var connectionString = Environment.GetEnvironmentVariable("SQLAZURECONNSTR_quickfeedbackdb");

builder.Services.AddDbContext<FeedbackDbContext>(options =>
{
    if (string.IsNullOrEmpty(connectionString))
        options.UseInMemoryDatabase("QuickFeedbackDb");
    else
        options.UseSqlServer(connectionString);
});

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

if (!string.IsNullOrEmpty(connectionString))
    app.UseHttpsRedirection();
app.UseCors("AllowFrontend");
app.UseAuthorization();
app.MapControllers();

// Auto-migrate database on startup (use EnsureCreated for in-memory, Migrate for SQL Server)
using (var scope = app.Services.CreateScope())
{
    var dbContext = scope.ServiceProvider.GetRequiredService<FeedbackDbContext>();
    if (string.IsNullOrEmpty(connectionString))
        dbContext.Database.EnsureCreated();
    else
        dbContext.Database.Migrate();
}

app.Run();
