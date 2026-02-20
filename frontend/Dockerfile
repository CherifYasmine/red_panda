# Build stage
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files
COPY package.json package-lock.json* ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Runtime stage
FROM node:20-alpine

WORKDIR /app

# Copy built files from builder
COPY --from=builder /app/dist ./dist

# Install serve to serve the static files
RUN npm install -g serve

# Expose port
EXPOSE 5173

# Set environment variables
ENV VITE_API_BASE_URL=http://backend:8080

# Run the application with serve
CMD ["serve", "-s", "dist", "-l", "5173"]
