"""
Script to create Android app icons from logo
Creates all required icon sizes for Android app
"""
import os
from PIL import Image

# Paths
LOGO_PATH = r"C:\Projects\Financial-management-Phuc-Dat\image\logo_phucdat.jpg"
RES_PATH = r"C:\Projects\FinancialmanagementPhucDatMobile\app\src\main\res"

# Icon sizes for different densities
ICON_SIZES = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

# Adaptive icon foreground size (should be 108dp = 432px for xxxhdpi)
ADAPTIVE_FOREGROUND_SIZE = 432

def create_icon(logo_path, output_path, size, is_round=False, padding_ratio=0.1):
    """Create icon with specified size"""
    try:
        # Open and resize logo
        img = Image.open(logo_path)
        
        # Convert to RGBA if needed
        if img.mode != 'RGBA':
            img = img.convert('RGBA')
        
        # Calculate size with padding (adaptive icons need safe zone)
        # Android adaptive icons have a safe zone of 66% in the center
        # So we add padding to ensure logo fits in safe zone
        padding = int(size * padding_ratio)
        max_logo_size = size - (padding * 2)
        
        # Resize maintaining aspect ratio
        img.thumbnail((max_logo_size, max_logo_size), Image.Resampling.LANCZOS)
        
        # Create square canvas with transparent background
        canvas = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        
        # Center the image on canvas with padding
        x_offset = (size - img.width) // 2
        y_offset = (size - img.height) // 2
        canvas.paste(img, (x_offset, y_offset), img)
        
        # Save as PNG
        canvas.save(output_path, 'PNG')
        print(f"OK: Created {output_path} ({size}x{size})")
        return True
    except Exception as e:
        print(f"ERROR: Creating {output_path}: {e}")
        return False

def create_adaptive_foreground(logo_path, output_path):
    """Create adaptive icon foreground with proper padding for safe zone"""
    # Adaptive icons need 17% padding on all sides (safe zone is 66% in center)
    return create_icon(logo_path, output_path, ADAPTIVE_FOREGROUND_SIZE, padding_ratio=0.17)

def create_adaptive_background(output_path, color=(255, 255, 255, 255)):
    """Create solid color background for adaptive icon"""
    try:
        bg = Image.new('RGBA', (ADAPTIVE_FOREGROUND_SIZE, ADAPTIVE_FOREGROUND_SIZE), color)
        bg.save(output_path, 'PNG')
        print(f"OK: Created background {output_path}")
        return True
    except Exception as e:
        print(f"ERROR: Creating background {output_path}: {e}")
        return False

def main():
    print("Creating Android app icons from logo...")
    print(f"Logo: {LOGO_PATH}")
    print(f"Output: {RES_PATH}\n")
    
    if not os.path.exists(LOGO_PATH):
        print(f"ERROR: Logo not found: {LOGO_PATH}")
        return
    
    # Create regular icons for each density
    for density, size in ICON_SIZES.items():
        density_path = os.path.join(RES_PATH, density)
        os.makedirs(density_path, exist_ok=True)
        
        # Regular icon
        icon_path = os.path.join(density_path, "ic_launcher.png")
        create_icon(LOGO_PATH, icon_path, size)
        
        # Round icon (same as regular for now)
        round_icon_path = os.path.join(density_path, "ic_launcher_round.png")
        create_icon(LOGO_PATH, round_icon_path, size)
    
    # Create adaptive icon foreground
    drawable_path = os.path.join(RES_PATH, "drawable")
    os.makedirs(drawable_path, exist_ok=True)
    
    foreground_path = os.path.join(drawable_path, "ic_launcher_foreground.png")
    create_adaptive_foreground(LOGO_PATH, foreground_path)
    
    # Create adaptive icon background (white)
    background_path = os.path.join(drawable_path, "ic_launcher_background.png")
    create_adaptive_background(background_path, (255, 255, 255, 255))
    
    print("\nSUCCESS: All icons created successfully!")
    print("\nNext steps:")
    print("1. Update mipmap-anydpi-v21/ic_launcher.xml to use new foreground")
    print("2. Update mipmap-anydpi-v26/ic_launcher.xml if exists")
    print("3. Rebuild the app")

if __name__ == "__main__":
    main()

