import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hash = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        
        // Verificar que el hash funcione
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verification: " + matches);
        
        // Verificar el hash actual
        String currentHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFiO5fKyTMR7Ux8cNy1Rjcr";
        boolean currentMatches = encoder.matches(password, currentHash);
        System.out.println("Current hash matches: " + currentMatches);
    }
} 