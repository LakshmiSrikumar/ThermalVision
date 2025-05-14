from PIL import Image, ImageEnhance
import numpy as np
from skimage import exposure, filters, color
import matplotlib.pyplot as plt

def pixel_to_temperature(pixel_value, mintemp, maxtemp):
    """
    Converts a pixel intensity (0-255) to temperature using linear scaling.
    Args:
        pixel_value (int): Pixel intensity (0-255).
        mintemp (float): Minimum temperature.
        maxtemp (float): Maximum temperature.
    Returns:
        float: Calculated temperature.
    """
    return mintemp + (pixel_value / 255.0) * (maxtemp - mintemp)

def enhance_image_auto(input_path, output_path):
    """
    Normalize a 16-bit TIFF to 8-bit and apply CLAHE, blurs, and brightness adjustment as needed.
    """
    img = Image.open(input_path)
    arr = np.array(img)
    arr_8bit = ((arr - arr.min()) / (arr.max() - arr.min()) * 255).astype(np.uint8)
    # CLAHE
    if arr_8bit.std() < 40:
        arr_8bit = exposure.equalize_adapthist(arr_8bit, clip_limit=0.02)
        arr_8bit = (arr_8bit * 255).astype(np.uint8)
    # Blur if noisy
    if filters.laplace(arr_8bit).var() > 100:
        arr_8bit = filters.median(arr_8bit)
    if filters.laplace(arr_8bit).var() > 100:
        arr_8bit = filters.gaussian(arr_8bit, sigma=1)
    # Brightness adjustment
    mean_intensity = arr_8bit.mean()
    if mean_intensity < 80 or mean_intensity > 180:
        factor = 128.0 / mean_intensity
        img_pil = Image.fromarray(arr_8bit)
        enhancer = ImageEnhance.Brightness(img_pil)
        img_pil = enhancer.enhance(factor)
        img_pil.save(output_path)
    else:
        Image.fromarray(arr_8bit).save(output_path)

def apply_inferno_colormap(input_path, output_path):
    """
    Convert image to a thermal visualization using a simple color mapping.
    Handles both grayscale and RGB input images.
    """
    # Open and convert to numpy array immediately
    img = Image.open(input_path)
    arr = np.array(img, dtype=np.float32)
    
    # If input is RGB, convert to grayscale first
    if len(arr.shape) == 3 and arr.shape[2] == 3:
        # Convert RGB to grayscale using standard weights
        arr = np.dot(arr[..., :3], [0.299, 0.587, 0.114])
    
    # Normalize to 0-255
    min_val = np.min(arr)
    max_val = np.max(arr)
    if max_val > min_val:
        arr = ((arr - min_val) / (max_val - min_val) * 255).astype(np.uint8)
    else:
        arr = np.zeros_like(arr, dtype=np.uint8)
    
    # Create RGB image
    rgb = np.zeros((arr.shape[0], arr.shape[1], 3), dtype=np.uint8)
    
    # Simple thermal color mapping
    # Cold (blue) -> Hot (red)
    rgb[:, :, 0] = arr  # Red channel
    rgb[:, :, 1] = arr // 2  # Green channel
    rgb[:, :, 2] = 255 - arr  # Blue channel
    
    # Save the result
    Image.fromarray(rgb).save(output_path)

def get_pixel_value(image_path, x, y):
    """
    Get the pixel value at the specified coordinates.
    For RGB images, returns the average of the RGB values.
    """
    img = Image.open(image_path)
    arr = np.array(img)
    
    # If input is RGB, convert to grayscale
    if len(arr.shape) == 3 and arr.shape[2] == 3:
        # Convert RGB to grayscale using standard weights
        arr = np.dot(arr[..., :3], [0.299, 0.587, 0.114])
    
    # Ensure coordinates are within bounds
    x = max(0, min(x, arr.shape[1] - 1))
    y = max(0, min(y, arr.shape[0] - 1))
    
    # Get the pixel value
    pixel_value = arr[y, x]
    
    # Convert to integer if it's a float
    if isinstance(pixel_value, np.ndarray):
        pixel_value = pixel_value.item()
    
    return int(pixel_value)
