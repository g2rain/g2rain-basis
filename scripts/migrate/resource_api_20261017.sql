ALTER TABLE resource_api
  ADD COLUMN openapi_document LONGTEXT NULL COMMENT '单接口最小 OpenAPI 文档' AFTER description,
  ADD COLUMN document_digest CHAR(64) NULL COMMENT '规范化单接口文档的 sha256' AFTER openapi_document;
