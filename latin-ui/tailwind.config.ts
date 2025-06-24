import type { Config } from "tailwindcss"

const config = {
  darkMode: ["class"],
  content: [
    "./pages/**/*.{ts,tsx}",
    "./components/**/*.{ts,tsx}",
    "./app/**/*.{ts,tsx}",
    "./src/**/*.{ts,tsx}",
    "*.{js,ts,jsx,tsx,mdx}",
  ],
  prefix: "",
  theme: {
    container: {
      center: true,
      padding: "2rem",
      screens: {
        "2xl": "1400px",
      },
    },
    extend: {
      colors: {
        border: "hsl(var(--border))",
        input: "hsl(var(--input))",
        ring: "hsl(var(--ring))",
        background: "hsl(var(--background))",
        foreground: "hsl(var(--foreground))",
        primary: {
          DEFAULT: "hsl(var(--primary))",
          foreground: "hsl(var(--primary-foreground))",
        },
        secondary: {
          DEFAULT: "hsl(var(--secondary))",
          foreground: "hsl(var(--secondary-foreground))",
        },
        destructive: {
          DEFAULT: "hsl(var(--destructive))",
          foreground: "hsl(var(--destructive-foreground))",
        },
        muted: {
          DEFAULT: "hsl(var(--muted))",
          foreground: "hsl(var(--muted-foreground))",
        },
        accent: {
          DEFAULT: "hsl(var(--accent))",
          foreground: "hsl(var(--accent-foreground))",
        },
        popover: {
          DEFAULT: "hsl(var(--popover))",
          foreground: "hsl(var(--popover-foreground))",
        },
        card: {
          DEFAULT: "hsl(var(--card))",
          foreground: "hsl(var(--card-foreground))",
        },
        "positive-trend": "#2e7d32",
        "negative-trend": "#ef6c00",
        "critical-alert": "#c62828",
        opportunity: "#c49b0b",
        "context-insight": "#1e88e5",
        "inactive-dormant": "#455a64",
        "strategic-action": "#e91e63",
        // New neutral shades
        "soft-neutral-white": "#F5F5F5", // Using F5F5F5 as primary
        "warm-whisper-grey": "#D8D8D8",
        "cool-silver-grey": "#B0B0B0",
        "pure-white": "#FFFFFF",
        // Original DT colors, can be removed if not directly used elsewhere
        "dt-magenta": "#E20074",
        "dt-green": "#43B02A",
        "dt-yellow": "#FFED00",
        "dt-red": "#D52B1E",
        "tile-inner-border-highlight-color": "var(--tile-inner-border-color-semantic)",
        // Contextual Accents for Canvas
        "accent-orange-pulse": "#FF8A65", // For "Uncover What Changed"
        "accent-yellow-calm": "#FFD54F", // For "Opportunity Timeline"
        "accent-blue-glow": "#4FC3F7", // For "Frontline Signals"
        "canvas-deep-dark": "#0E0E0E",
        "panel-dark": "#181818",
        "warm-grey-888": "#888888",
        "dt-magenta-rgb": "226, 0, 116", // Added for shadow tinting
      },
      borderRadius: {
        lg: "var(--radius)",
        md: "calc(var(--radius) - 2px)",
        sm: "calc(var(--radius) - 4px)",
      },
      backgroundImage: {
        "gradient-radial": "radial-gradient(var(--tw-gradient-stops))",
        "gradient-conic": "conic-gradient(from 180deg at 50% 50%, var(--tw-gradient-stops))",
        // Masaic signature gradients
        "masaic-brand": "linear-gradient(135deg, hsl(var(--accent)) 0%, hsl(var(--primary)) 100%)",
        "masaic-glow": "radial-gradient(circle at center, hsla(var(--accent), 0.3) 0%, transparent 70%)",
      },
      boxShadow: {
        "tile-glow-semantic": "0 0 15px 8px var(--tile-glow-color-semantic)",
        "modal-glow": "0 0 60px 15px rgba(0,0,0,0.1), 0 0 70px 20px rgba(var(--dt-magenta-rgb),0.08)", // Adjusted for a softer, slightly tinted glow
        "modal-glow-dark": "0 0 60px 15px rgba(0,0,0,0.2), 0 0 80px 25px rgba(255,255,255,0.06)",
        "canvas-processing-glow":
          "0 0 15px 4px rgba(var(--accent-color-dynamic), 0.5), 0 0 25px 10px rgba(var(--accent-color-dynamic), 0.3)",
        // Masaic signature shadows
        "masaic-depth": "0 8px 32px -8px rgba(0,0,0,0.3), 0 0 0 1px rgba(255,255,255,0.05)",
        "masaic-float": "0 4px 20px -4px rgba(0,0,0,0.25), 0 0 0 1px rgba(255,255,255,0.08)",
      },
      keyframes: {
        "accordion-down": {
          from: { height: "0" },
          to: { height: "var(--radix-accordion-content-height)" },
        },
        "accordion-up": {
          from: { height: "var(--radix-accordion-content-height)" },
          to: { height: "0" },
        },
        "breathing-glow": {
          "0%, 100%": {
            boxShadow:
              "0 0 8px 2px rgba(var(--accent-color-dynamic), 0.3), 0 0 12px 5px rgba(var(--accent-color-dynamic), 0.2)",
          },
          "50%": {
            boxShadow:
              "0 0 12px 4px rgba(var(--accent-color-dynamic), 0.5), 0 0 18px 8px rgba(var(--accent-color-dynamic), 0.35)",
          },
        },
        shimmer: {
          "0%": { backgroundPosition: "-1000px 0" },
          "100%": { backgroundPosition: "1000px 0" },
        },
        "pulse-faint": {
          "0%, 100%": { opacity: "0.7" },
          "50%": { opacity: "1" },
        },
        // Masaic signature animations
        "masaic-rise": {
          "0%": { 
            opacity: "0", 
            transform: "translateY(20px) scale(0.95)",
          },
          "100%": { 
            opacity: "1", 
            transform: "translateY(0) scale(1)",
          },
        },
        "masaic-glow-pulse": {
          "0%, 100%": { 
            boxShadow: "0 0 20px rgba(hsl(var(--accent)), 0.4)",
          },
          "50%": { 
            boxShadow: "0 0 40px rgba(hsl(var(--accent)), 0.8)",
          },
        },
        "magic-glow": {
          "0%, 100%": { 
            backgroundPosition: "0% 50%",
          },
          "50%": { 
            backgroundPosition: "100% 50%",
          },
        },
      },
      animation: {
        "accordion-down": "accordion-down 0.2s ease-out",
        "accordion-up": "accordion-up 0.2s ease-out",
        "breathing-glow": "breathing-glow 3s ease-in-out infinite",
        shimmer: "shimmer 2s linear infinite",
        "pulse-faint": "pulse-faint 2s cubic-bezier(0.4, 0, 0.6, 1) infinite",
        // Masaic signature animations
        "masaic-rise": "masaic-rise 0.6s cubic-bezier(0.16, 1, 0.3, 1)",
        "masaic-glow-pulse": "masaic-glow-pulse 3s ease-in-out infinite",
        "magic-glow": "magic-glow 3s ease-in-out infinite",
      },
      fontFamily: {
        sans: [
          "var(--font-sans)",
          "system-ui",
          "-apple-system",
          "BlinkMacSystemFont",
          "Segoe UI",
          "Roboto",
          "Helvetica Neue",
          "Arial",
          "Noto Sans",
          "sans-serif",
          "Apple Color Emoji",
          "Segoe UI Emoji",
          "Segoe UI Symbol",
          "Noto Color Emoji",
        ],
      },
      // Masaic typography enhancements
      letterSpacing: {
        'masaic': '0.02em',
        'masaic-wide': '0.05em',
      },
      textShadow: {
        'sm': '0 1px 2px rgba(0, 0, 0, 0.1)',
        'md': '0 2px 4px rgba(0, 0, 0, 0.1)',
      },
    },
  },
  plugins: [
    require("tailwindcss-animate"),
    // Masaic custom plugin for additional utilities
    function({ addUtilities }: any) {
      const newUtilities = {
        '.text-shadow-sm': {
          textShadow: '0 1px 2px rgba(0, 0, 0, 0.1)',
        },
        '.text-shadow-md': {
          textShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
        },
        '.masaic-transition': {
          transition: 'all 0.3s cubic-bezier(0.16, 1, 0.3, 1)',
        },
        '.masaic-border': {
          border: '1px solid rgba(255, 255, 255, 0.1)',
          borderRadius: '12px',
        },
      }
      addUtilities(newUtilities)
    }
  ],
} satisfies Config

export default config
